package com.qimu.qiapibackend.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.lly835.bestpay.constants.AliPayConstants;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.config.AliPayAccountConfig;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.mapper.ProductOrderMapper;
import com.qimu.qiapibackend.model.entity.ProductInfo;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.enums.PaymentStatusEnum;
import com.qimu.qiapibackend.model.vo.PaymentInfoVo;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;
import com.qimu.qiapibackend.service.PaymentInfoService;
import com.qimu.qiapibackend.service.ProductOrderService;
import com.qimu.qiapibackend.service.UserService;
import com.qimu.qiapibackend.service.alipay.AliPayResponse;
import com.qimu.qiapibackend.service.alipay.BestPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qimu.qiapibackend.constant.PayConstant.ORDER_PREFIX;
import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.ALIPAY;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.SUCCESS;


/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:18:35
 * @Version: 1.0
 * @Description: 接口顺序服务impl
 */
@Service
@Slf4j
@Qualifier("ALIPAY")
public class AlipayOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder> implements ProductOrderService {

    @Resource
    AliPayAccountConfig aliPayAccountConfig;
    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private ProductInfoServiceImpl productInfoService;
    @Resource
    private BestPayServiceImpl bestPayService;
    @Resource
    private PaymentInfoService paymentInfoService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public ProductOrderVo getProductOrder(Long productId, User loginUser, String payType) {
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getProductId, productId);
        lambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getValue());
        lambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
        lambdaQueryWrapper.eq(ProductOrder::getUserId, loginUser.getId());
        synchronized (String.valueOf(loginUser.getId()).intern()) {
            ProductOrder oldOrder = this.getOne(lambdaQueryWrapper);
            if (oldOrder == null) {
                return null;
            }
            ProductOrderVo productOrderVo = new ProductOrderVo();
            BeanUtils.copyProperties(oldOrder, productOrderVo);
            productOrderVo.setProductInfo(JSONUtil.toBean(oldOrder.getProductInfo(), ProductInfo.class));
            return productOrderVo;
        }
    }

    @Override
    public ProductOrderVo saveProductOrder(Long productId, User loginUser) {
        ProductInfo productInfo = productInfoService.getById(productId);
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }

        // 5分钟有效期
        Date date = DateUtil.date(System.currentTimeMillis());
        Date expirationTime = DateUtil.offset(date, DateField.MINUTE, 5);
        String orderNo = ORDER_PREFIX + RandomUtil.randomNumbers(20);

        ProductOrder productOrder = new ProductOrder();
        productOrder.setUserId(loginUser.getId());
        productOrder.setOrderNo(orderNo);
        productOrder.setProductId(productInfo.getId());
        productOrder.setOrderName(productInfo.getName());
        productOrder.setTotal(productInfo.getTotal());
        productOrder.setStatus(PaymentStatusEnum.NOTPAY.getValue());
        productOrder.setPayType(ALIPAY.getValue());
        productOrder.setExpirationTime(expirationTime);
        productOrder.setProductInfo(JSONUtil.toJsonPrettyStr(productInfo));
        productOrder.setAddPoints(productInfo.getAddPoints());

        synchronized (loginUser.getUserAccount().intern()) {
            boolean saveResult = this.save(productOrder);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            // 构建创建支付宝订单请求
            PayRequest payRequest = new PayRequest();
            payRequest.setPayTypeEnum(BestPayTypeEnum.ALIPAY_PC);
            payRequest.setOrderId(orderNo);
            Integer total = productInfo.getTotal();
            payRequest.setOrderAmount(total / 100.0);
            payRequest.setAttach(productInfo.getDescription());
            payRequest.setOrderName(productInfo.getName());
            // 发送请求
            PayResponse pay = bestPayService.pay(payRequest);
            pay.getBody();
            if (StringUtils.isBlank(pay.getBody())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            productOrder.setFormData(pay.getBody());
            boolean updateResult = this.updateProductOrder(productOrder);
            if (!updateResult) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            // 构建vo
            ProductOrderVo productOrderVo = new ProductOrderVo();
            BeanUtils.copyProperties(productOrder, productOrderVo);
            productOrderVo.setProductInfo(productInfo);
            return productOrderVo;
        }
    }

    @Override
    public boolean updateProductOrder(ProductOrder productOrder) {
        String formData = productOrder.getFormData();
        Long id = productOrder.getId();
        ProductOrder updateCodeUrl = new ProductOrder();
        updateCodeUrl.setFormData(formData);
        updateCodeUrl.setId(id);
        return this.updateById(updateCodeUrl);
    }

    @Override
    public boolean updateOrderStatusByOrderNo(String outTradeNo, String orderStatus) {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setStatus(orderStatus);
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getOrderNo, outTradeNo);
        return this.update(productOrder, lambdaQueryWrapper);
    }

    @Override
    public ProductOrder getProductOrderByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<ProductOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductOrder::getOrderNo, outTradeNo);
        return this.getOne(lambdaQueryWrapper);
    }

    /**
     * 查找超过minutes分钟并且未支付的的订单
     *
     * @param minutes 分钟
     * @return {@link List}<{@link ProductOrder}>
     */
    @Override
    public List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove) {
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
        LambdaQueryWrapper<ProductOrder> productOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productOrderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getValue());
        // 大于5分钟表示删除
        if (remove) {
            productOrderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.CLOSED.getValue());
        }
        productOrderLambdaQueryWrapper.and(p -> p.le(ProductOrder::getCreateTime, instant));
        return this.list(productOrderLambdaQueryWrapper);
    }

    @Override
    public void checkOrderStatus(ProductOrder productOrder) throws WxPayException {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doPaymentNotify(String notifyData, HttpServletRequest request) {
        log.info("【异步通知】支付宝平台的数据request={}", notifyData);
        AliPayResponse response = bestPayService.asyncNotify(notifyData);
        String result = checkAlipayOrder(response);
        if (!"success".equals(result)) {
            return result;
        }
        // 处理业务
        RLock rLock = redissonClient.getLock("notify:AlipayOrder:lock");
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                String doAliPayOrderBusinessResult = this.doAliPayOrderBusiness(response);
                if (StringUtils.isBlank(doAliPayOrderBusinessResult)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                }
                return doAliPayOrderBusinessResult;
            }
        } catch (Exception e) {
            log.error("【支付宝】:" + JSONUtil.toJsonStr(response));
            throw new RuntimeException(e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
        log.info("【支付宝】：校验成功");
        return "success";
    }

    private String checkAlipayOrder(AliPayResponse response) {
        // 1.验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
        ProductOrder productOrder = this.getProductOrderByOutTradeNo(response.getOutTradeNo());
        String result = "failure";
        if (productOrder == null) {
            log.error("订单不存在");
            return result;
        }
        // 2.判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
        int totalAmount = new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal("100")).intValue();
        if (totalAmount != productOrder.getTotal()) {
            log.error("订单金额不一致");
            return result;
        }
        // 3.校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
        String sellerId = aliPayAccountConfig.getSellerId();
        if (!response.getSellerId().equals(sellerId)) {
            log.error("卖家账号校验失败");
            return result;
        }
        // 4.验证 app_id 是否为该商家本身。
        String appId = aliPayAccountConfig.getAppId();
        if (!response.getAppId().equals(appId)) {
            log.error("校验失败");
            return result;
        }
        // 状态 TRADE_SUCCESS 的通知触发条件是商家开通的产品支持退款功能的前提下，买家付款成功。
        String tradeStatus = response.getTradeStatus();
        if (!tradeStatus.equals(AliPayConstants.TRADE_SUCCESS)) {
            log.error("交易失败");
            return result;
        }
        return "success";
    }

    protected String doAliPayOrderBusiness(AliPayResponse response) {
        String outTradeNo = response.getOutTradeNo();
        ProductOrder productOrder = this.getProductOrderByOutTradeNo(outTradeNo);
        // 处理重复通知
        if (SUCCESS.getValue().equals(productOrder.getStatus())) {
            return "success";
        }
        // 业务代码
        // 更新订单状态
        boolean updateOrderStatus = this.updateOrderStatusByOrderNo(outTradeNo, SUCCESS.getValue());
        // 更新用户积分
        boolean updateWalletBalance = userService.updateWalletBalance(productOrder.getUserId(), productOrder.getAddPoints());
        // 保存支付记录
        PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
        paymentInfoVo.setAppid(response.getAppId());
        paymentInfoVo.setOutTradeNo(response.getOutTradeNo());
        paymentInfoVo.setTransactionId(response.getTradeNo());
        paymentInfoVo.setTradeType(ALIPAY.getValue());
        paymentInfoVo.setTradeState(response.getTradeStatus());
        paymentInfoVo.setTradeStateDesc("支付成功");
        WxPayOrderNotifyV3Result.Payer payer = new WxPayOrderNotifyV3Result.Payer();
        payer.setOpenid(response.getBuyerId());
        paymentInfoVo.setPayer(payer);
        WxPayOrderNotifyV3Result.Amount amount = new WxPayOrderNotifyV3Result.Amount();
        amount.setTotal(new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal("100")).intValue());
        amount.setPayerTotal(new BigDecimal(response.getReceiptAmount()).multiply(new BigDecimal("100")).intValue());
        amount.setCurrency("CNY");
        amount.setPayerCurrency("CNY");
        paymentInfoVo.setAmount(amount);
        boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
        if (paymentResult && updateOrderStatus && updateWalletBalance) {
            log.info("【支付回调通知处理成功】");
            return "success";
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR);
    }
}




