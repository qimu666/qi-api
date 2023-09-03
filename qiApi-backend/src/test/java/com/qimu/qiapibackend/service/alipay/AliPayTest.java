package com.qimu.qiapibackend.service.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ijpay.alipay.AliPayApi;
import com.qimu.qiapibackend.config.AliPayAccountConfig;
import com.qimu.qiapibackend.model.enums.AlipayTradeStatusEnum;
import com.qimu.qiapibackend.model.enums.PaymentStatusEnum;
import com.qimu.qiapibackend.service.ProductOrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.qimu.qiapibackend.constant.PayConstant.RESPONSE_CODE_SUCCESS;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.NOTPAY;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.SUCCESS;


/**
 * @Author: QiMu
 * @Date: 2023年08月24日 22:19
 * @Version: 1.0
 * @Description:
 */
@SpringBootTest
@Slf4j
public class AliPayTest {

    @Resource
    AliPayAccountConfig aliPayAccountConfig;
    String OutTradeNo = "order_97460568736566704490";
    @Resource
    @Qualifier("ALIPAY")
    private ProductOrderService productOrderService;

    @SneakyThrows
    @Test
    void alipayQueryOrder() {
        // 查询订单
        AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
        alipayTradeQueryModel.setOutTradeNo(OutTradeNo);
        AlipayTradeQueryResponse alipayTradeQueryResponse = AliPayApi.tradeQueryToResponse(alipayTradeQueryModel);

        // 本地创建了订单,但是用户没有扫码,支付宝端没有订单
        if (!alipayTradeQueryResponse.getCode().equals(RESPONSE_CODE_SUCCESS)) {
            // 更新本地订单状态
            productOrderService.updateOrderStatusByOrderNo(OutTradeNo, PaymentStatusEnum.CLOSED.getValue());
            log.info("超时订单{},更新成功", OutTradeNo);
        }
        String tradeStatus = AlipayTradeStatusEnum.findByName(alipayTradeQueryResponse.getTradeStatus()).getPaymentStatusEnum().getValue();
        // 订单没有支付就关闭订单,更新本地订单状态
        if (tradeStatus.equals(NOTPAY.getValue())) {
            AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
            alipayTradeCloseModel.setOutTradeNo(OutTradeNo);
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            request.setBizModel(alipayTradeCloseModel);
            AliPayApi.doExecute(request);
            productOrderService.updateOrderStatusByOrderNo(OutTradeNo, PaymentStatusEnum.CLOSED.getValue());
            log.info("超时订单{},关闭成功", OutTradeNo);
        }
        if (tradeStatus.equals(SUCCESS.getValue())) {
            // 订单已支付更新商户端的订单状态
            productOrderService.updateOrderStatusByOrderNo(OutTradeNo, SUCCESS.getValue());
            // 补发积分到用户钱包
            // userService.addWalletBalance(productOrder.getUserId(), productOrder.getAddPoints());
            // paymentInfoService.createPaymentInfo(paymentInfoVo);
            // 保存支付记录
            // PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
            // paymentInfoVo.setAppid(response.getAppId());
            // paymentInfoVo.setOutTradeNo(response.getOutTradeNo());
            // paymentInfoVo.setTransactionId(response.getTradeNo());
            // paymentInfoVo.setTradeType(ALIPAY.getValue());
            // paymentInfoVo.setTradeState(response.getTradeStatus());
            // paymentInfoVo.setTradeStateDesc("支付成功");
            // WxPayOrderNotifyV3Result.Payer payer = new WxPayOrderNotifyV3Result.Payer();
            // payer.setOpenid(response.getBuyerId());
            // paymentInfoVo.setPayer(payer);
            // WxPayOrderNotifyV3Result.Amount amount = new WxPayOrderNotifyV3Result.Amount();
            // amount.setTotal(new BigDecimal(response.getTotalAmount()).multiply(new BigDecimal("100")).intValue());
            // amount.setPayerTotal(new BigDecimal(response.getReceiptAmount()).multiply(new BigDecimal("100")).intValue());
            // amount.setCurrency("CNY");
            // amount.setPayerCurrency("CNY");
            // paymentInfoVo.setAmount(amount);
        }
    }

    @SneakyThrows
    @Test
    void alipayCreateOrder() {

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(OutTradeNo);
        model.setSubject("ttt");
        model.setTotalAmount("0.01");
        model.setBody("teee");


        AlipayTradePagePayRequest alipay_request = new AlipayTradePagePayRequest();
        alipay_request.setBizModel(model);
        alipay_request.setNotifyUrl(aliPayAccountConfig.getNotifyUrl());
        alipay_request.setReturnUrl(aliPayAccountConfig.getReturnUrl());
        AlipayTradePagePayResponse alipayTradePagePayResponse = AliPayApi.pageExecute(alipay_request);
        String payUrl = alipayTradePagePayResponse.getBody();
        System.err.println(payUrl);
        System.err.println(alipayTradePagePayResponse);
    }

    @Test
    void clonesOrder() throws AlipayApiException {
        AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
        alipayTradeCloseModel.setOutTradeNo(OutTradeNo);
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizModel(alipayTradeCloseModel);
        AlipayTradeCloseResponse alipayTradeCloseResponse = AliPayApi.doExecute(request);
        System.err.println(alipayTradeCloseResponse.getBody());
    }

    @Test
    void test() {
        BigDecimal scaledAmount = new BigDecimal("1.9").divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        System.err.println("BigDecimal=" + scaledAmount);
        double a = 1.9 / 100;
        System.err.println(a);
    }
}
