package com.qimu.qiapibackend.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.config.AliPayAccountConfig;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.mapper.ProductOrderMapper;
import com.qimu.qiapibackend.model.alipay.AliPayAsyncResponse;
import com.qimu.qiapibackend.model.entity.ProductInfo;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.enums.AlipayTradeStatusEnum;
import com.qimu.qiapibackend.model.enums.PaymentStatusEnum;
import com.qimu.qiapibackend.model.vo.PaymentInfoVo;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;
import com.qimu.qiapibackend.service.PaymentInfoService;
import com.qimu.qiapibackend.service.ProductOrderService;
import com.qimu.qiapibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.qimu.qiapibackend.constant.PayConstant.*;
import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.ALIPAY;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.NOTPAY;
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
    private AliPayAccountConfig aliPayAccountConfig;
    @Resource
    private UserService userService;
    @Resource
    private ProductInfoServiceImpl productInfoService;

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

            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(orderNo);
            model.setSubject(productInfo.getName());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            // 金额四舍五入
            BigDecimal scaledAmount = new BigDecimal(productInfo.getTotal()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            model.setTotalAmount(String.valueOf(scaledAmount));
            model.setBody(productInfo.getDescription());

            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setBizModel(model);
            request.setNotifyUrl(aliPayAccountConfig.getNotifyUrl());
            request.setReturnUrl(aliPayAccountConfig.getReturnUrl());

            try {
                AlipayTradePagePayResponse alipayTradePagePayResponse = AliPayApi.pageExecute(request);
                String payUrl = alipayTradePagePayResponse.getBody();
                productOrder.setFormData(payUrl);
            } catch (AlipayApiException e) {
                throw new RuntimeException(e);
            }

            boolean updateResult = this.updateProductOrder(productOrder);
            if (!updateResult & !saveResult) {
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processingTimedOutOrders(ProductOrder productOrder) {
        String orderNo = productOrder.getOrderNo();
        try {
            // 查询订单
            AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
            alipayTradeQueryModel.setOutTradeNo(orderNo);
            AlipayTradeQueryResponse alipayTradeQueryResponse = AliPayApi.tradeQueryToResponse(alipayTradeQueryModel);

            // 本地创建了订单,但是用户没有扫码,支付宝端没有订单
            if (!alipayTradeQueryResponse.getCode().equals(RESPONSE_CODE_SUCCESS)) {
                // 更新本地订单状态
                this.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.CLOSED.getValue());
                log.info("超时订单{},更新成功", orderNo);
                return;
            }
            String tradeStatus = AlipayTradeStatusEnum.findByName(alipayTradeQueryResponse.getTradeStatus()).getPaymentStatusEnum().getValue();
            // 订单没有支付就关闭订单,更新本地订单状态
            if (tradeStatus.equals(NOTPAY.getValue())) {
                AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
                alipayTradeCloseModel.setOutTradeNo(orderNo);
                AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
                request.setBizModel(alipayTradeCloseModel);
                AliPayApi.doExecute(request);
                this.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.CLOSED.getValue());
                log.info("超时订单{},关闭成功", orderNo);
                return;
            }
            if (tradeStatus.equals(SUCCESS.getValue())) {
                // 订单已支付更新商户端的订单状态
                boolean updateOrderStatus = this.updateOrderStatusByOrderNo(orderNo, SUCCESS.getValue());
                // 补发积分到用户钱包
                boolean updateWalletBalance = userService.updateWalletBalance(productOrder.getUserId(), productOrder.getAddPoints());
                // 保存支付记录
                PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
                paymentInfoVo.setAppid(aliPayAccountConfig.getAppId());
                paymentInfoVo.setOutTradeNo(alipayTradeQueryResponse.getOutTradeNo());
                paymentInfoVo.setTransactionId(alipayTradeQueryResponse.getTradeNo());
                paymentInfoVo.setTradeType("电脑网站支付");
                paymentInfoVo.setTradeState(alipayTradeQueryResponse.getTradeStatus());
                paymentInfoVo.setTradeStateDesc("支付成功");
                paymentInfoVo.setSuccessTime(String.valueOf(alipayTradeQueryResponse.getSendPayDate()));
                WxPayOrderNotifyV3Result.Payer payer = new WxPayOrderNotifyV3Result.Payer();
                payer.setOpenid(alipayTradeQueryResponse.getBuyerOpenId());
                paymentInfoVo.setPayer(payer);
                WxPayOrderNotifyV3Result.Amount amount = new WxPayOrderNotifyV3Result.Amount();
                amount.setTotal(new BigDecimal(alipayTradeQueryResponse.getTotalAmount()).multiply(new BigDecimal("100")).intValue());
                amount.setPayerTotal(new BigDecimal(alipayTradeQueryResponse.getReceiptAmount()).multiply(new BigDecimal("100")).intValue());
                amount.setCurrency(alipayTradeQueryResponse.getPayCurrency());
                amount.setPayerCurrency(alipayTradeQueryResponse.getPayCurrency());
                paymentInfoVo.setAmount(amount);
                boolean paymentResult = paymentInfoService.createPaymentInfo(paymentInfoVo);
                if (!updateOrderStatus & !updateWalletBalance & !paymentResult) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                }
                log.info("超时订单{},更新成功", orderNo);
            }
        } catch (AlipayApiException e) {
            log.error("订单{} 处理失败", orderNo);
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doPaymentNotify(String notifyData, HttpServletRequest request) {
        // 处理业务
        RLock rLock = redissonClient.getLock("notify:AlipayOrder:lock");
        Map<String, String> params = AliPayApi.toMap(request);
        AliPayAsyncResponse aliPayAsyncResponse = JSONUtil.toBean(JSONUtil.toJsonStr(params), AliPayAsyncResponse.class);
        try {
            String result = checkAlipayOrder(aliPayAsyncResponse, params);
            if (!"success".equals(result)) {
                return result;
            }
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                String doAliPayOrderBusinessResult = this.doAliPayOrderBusiness(aliPayAsyncResponse);
                if (StringUtils.isBlank(doAliPayOrderBusinessResult)) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                }
                return doAliPayOrderBusinessResult;
            }
        } catch (Exception e) {
            log.error("【支付宝异步回调异常】:" + e.getMessage());
            return "failure";
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
        log.info("【支付宝】：校验成功");
        return "success";
    }

    private String checkAlipayOrder(AliPayAsyncResponse response, Map<String, String> params) throws AlipayApiException {
        String result = "failure";
        boolean verifyResult = AlipaySignature.rsaCheckV1(params, AliPayApiConfigKit.getAliPayApiConfig().getAliPayPublicKey(),
                AliPayApiConfigKit.getAliPayApiConfig().getCharset(),
                AliPayApiConfigKit.getAliPayApiConfig().getSignType());
        if (!verifyResult) {
            return result;
        }
        // 1.验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
        ProductOrder productOrder = this.getProductOrderByOutTradeNo(response.getOutTradeNo());
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
        if (!tradeStatus.equals(TRADE_SUCCESS)) {
            log.error("交易失败");
            return result;
        }
        return "success";
    }

    protected String doAliPayOrderBusiness(AliPayAsyncResponse response) {
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
        paymentInfoVo.setTradeType("电脑网站支付");
        paymentInfoVo.setTradeState(response.getTradeStatus());
        paymentInfoVo.setTradeStateDesc("支付成功");
        paymentInfoVo.setSuccessTime(response.getNotifyTime());
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




