package com.qimu.qiapibackend.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.qimu.qiapibackend.common.BaseResponse;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.common.ResultUtils;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.model.dto.InterfaceOrder.InterfaceOrderAddRequest;
import com.qimu.qiapibackend.model.dto.InterfaceOrder.InterfaceOrderQueryRequest;
import com.qimu.qiapibackend.model.entity.InterfaceOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.vo.InterfaceOrderVo;
import com.qimu.qiapibackend.model.vo.PaymentInfoVo;
import com.qimu.qiapibackend.service.InterfaceOrderService;
import com.qimu.qiapibackend.service.PaymentInfoService;
import com.qimu.qiapibackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.qimu.qiapibackend.constant.PayConstant.ORDER_PREFIX;
import static com.qimu.qiapibackend.constant.PayConstant.QUERY_ORDER_STATUS;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.SUCCESS;


/**
 * @Author: QiMu
 * @Date: 2023年08月23日 00:13
 * @Version: 1.0
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class PayController {
    @Resource
    private WxPayService wxPayService;
    @Resource
    private PaymentInfoService paymentInfoService;
    @Resource
    private UserService userService;
    @Resource
    private InterfaceOrderService interfaceOrderService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    /**
     * 创建订单
     *
     * @param interfaceOrderAddRequest 接口订单添加请求
     * @param request                  要求
     * @return {@link BaseResponse}<{@link InterfaceOrderVo}>
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<InterfaceOrderVo> createOrder(@RequestBody InterfaceOrderAddRequest interfaceOrderAddRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(interfaceOrderAddRequest, interfaceOrderAddRequest.getInterfaceId()) || interfaceOrderAddRequest.getInterfaceId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long interfaceId = interfaceOrderAddRequest.getInterfaceId();
        User loginUser = userService.getLoginUser(request);

        InterfaceOrderVo interfaceOrderVo = interfaceOrderService.getInterfaceOrder(interfaceId, loginUser);
        if (interfaceOrderVo != null) {
            return ResultUtils.success(interfaceOrderVo);
        }

        String orderNo = ORDER_PREFIX + RandomUtil.randomNumbers(20);
        InterfaceOrder interfaceOrder = interfaceOrderService.saveInterfaceOrder(interfaceId, orderNo, loginUser);
        WxPayUnifiedOrderV3Request wxPayRequest = new WxPayUnifiedOrderV3Request();
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(interfaceOrder.getTotal());
        wxPayRequest.setAmount(amount);
        wxPayRequest.setDescription(interfaceOrder.getOrderName());
        // 设置订单的过期时间为5分钟
        Date date = DateUtil.date(System.currentTimeMillis());
        Date offset = DateUtil.offset(date, DateField.MINUTE, 5);
        String format = DateUtil.format(offset, "yyyy-MM-dd'T'HH:mm:ssXXX");
        wxPayRequest.setTimeExpire(format);
        wxPayRequest.setOutTradeNo(interfaceOrder.getOrderNo());
        String codeUrl = null;
        try {
            codeUrl = wxPayService.createOrderV3(TradeTypeEnum.NATIVE, wxPayRequest);
            boolean result = interfaceOrderService.updateInterfaceOrder(codeUrl, interfaceOrder.getId());
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            interfaceOrder.setCodeUrl(codeUrl);
        } catch (WxPayException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
        interfaceOrderVo = new InterfaceOrderVo();
        BeanUtils.copyProperties(interfaceOrder, interfaceOrderVo);
        return ResultUtils.success(interfaceOrderVo);
    }

    /**
     * 查询订单状态
     *
     * @param interfaceOrderQueryRequest 接口订单查询请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/query/status")
    public BaseResponse<Boolean> queryOrderStatus(@RequestBody InterfaceOrderQueryRequest interfaceOrderQueryRequest) {
        if (ObjectUtils.isEmpty(interfaceOrderQueryRequest) || StringUtils.isBlank(interfaceOrderQueryRequest.getOrderNo())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String orderNo = interfaceOrderQueryRequest.getOrderNo();
        Boolean data = redisTemplate.opsForValue().get(QUERY_ORDER_STATUS + orderNo);
        if (Boolean.FALSE.equals(data)) {
            return ResultUtils.success(data);
        }
        String orderStatus = interfaceOrderService.getInterfaceOrderStatus(orderNo);
        if (SUCCESS.getValue().equals(orderStatus)) {
            return ResultUtils.success(true);
        }
        redisTemplate.opsForValue().set(QUERY_ORDER_STATUS + orderNo, false, 5, TimeUnit.MINUTES);
        return ResultUtils.success(false);
    }

    /**
     * 解析订单通知结果
     * 通知频率为15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h - 总计 24h4m
     *
     * @param notifyData 通知数据
     * @param request    请求
     * @return {@link String}
     * @throws WxPayException wx支付异常
     */
    @ApiOperation("支付回调通知处理")
    @PostMapping("/notify/order")
    public String parseOrderNotifyResult(@RequestBody String notifyData, HttpServletRequest request) throws WxPayException {
        RLock rLock = redissonClient.getLock("notify:order:lock");
        try {
            log.info("【支付回调通知处理】:{}", notifyData);
            WxPayOrderNotifyV3Result result = wxPayService.parseOrderNotifyV3Result(notifyData, this.getRequestHeader(request));
            // 解密后的数据
            WxPayOrderNotifyV3Result.DecryptNotifyResult notifyResult = result.getResult();
            if (WxPayConstants.WxpayTradeStatus.SUCCESS.equals(notifyResult.getTradeState())) {
                String outTradeNo = notifyResult.getOutTradeNo();
                if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    String orderStatus = interfaceOrderService.getInterfaceOrderStatus(outTradeNo);
                    if (SUCCESS.getValue().equals(orderStatus)) {
                        return WxPayNotifyResponse.success("支付成功");
                    }
                    interfaceOrderService.updateOrderStatusByOrderNo(outTradeNo, SUCCESS.getValue());
                    redisTemplate.delete(QUERY_ORDER_STATUS + outTradeNo);
                    PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
                    BeanUtils.copyProperties(notifyResult, paymentInfoVo);
                    paymentInfoService.createPaymentInfo(paymentInfoVo);
                    log.info("【支付回调通知处理成功】");
                }
            }
            if (WxPayConstants.WxpayTradeStatus.PAY_ERROR.equals(notifyResult.getTradeState())) {
                log.error("【支付失败】" + request);
                throw new WxPayException("支付失败");
            }
            if (WxPayConstants.WxpayTradeStatus.USER_PAYING.equals(notifyResult.getTradeState())) {
                throw new WxPayException("支付中");
            }
            return WxPayNotifyResponse.success("支付成功");
        } catch (Exception e) {
            log.error("【支付失败】" + request);
            throw new WxPayException("支付失败");
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }


    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    public SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }
}
