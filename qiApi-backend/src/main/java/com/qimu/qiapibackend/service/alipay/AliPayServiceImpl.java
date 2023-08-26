package com.qimu.qiapibackend.service.alipay;

import com.lly835.bestpay.constants.AliPayConstants;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.alipay.request.AliPayPcRequest;
import com.lly835.bestpay.service.impl.alipay.*;
import com.lly835.bestpay.utils.JsonUtil;
import com.lly835.bestpay.utils.MapUtil;
import com.lly835.bestpay.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: QiMu
 * @Date: 2023年08月26日 11:26
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class AliPayServiceImpl extends com.lly835.bestpay.service.impl.alipay.AliPayServiceImpl {

    @Override
    public PayResponse pay(PayRequest request) {
        if (request.getPayTypeEnum() == BestPayTypeEnum.ALIPAY_H5) {
            AlipayH5ServiceImpl alipayH5Service = new AlipayH5ServiceImpl();
            alipayH5Service.setAliPayConfig(aliPayConfig);
            return alipayH5Service.pay(request);
        } else if (request.getPayTypeEnum() == BestPayTypeEnum.ALIPAY_QRCODE) {
            AlipayQRCodeServiceImpl alipayQRCodeService = new AlipayQRCodeServiceImpl();
            alipayQRCodeService.setAliPayConfig(aliPayConfig);
            return alipayQRCodeService.pay(request);
        } else if (request.getPayTypeEnum() == BestPayTypeEnum.ALIPAY_BARCODE) {
            AlipayBarCodeServiceImpl alipayBarCodeService = new AlipayBarCodeServiceImpl();
            alipayBarCodeService.setAliPayConfig(aliPayConfig);
            return alipayBarCodeService.pay(request);
        } else if (request.getPayTypeEnum() == BestPayTypeEnum.ALIPAY_APP) {
            AlipayAppServiceImpl alipayAppService = new AlipayAppServiceImpl();
            alipayAppService.setAliPayConfig(aliPayConfig);
            return alipayAppService.pay(request);
        }
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("out_trade_no", request.getOrderId());
        AliPayPcRequest aliPayRequest = new AliPayPcRequest();
        if (request.getPayTypeEnum() == BestPayTypeEnum.ALIPAY_PC) {
            requestParams.put("product_code", AliPayConstants.FAST_INSTANT_TRADE_PAY);
            aliPayRequest.setMethod(AliPayConstants.ALIPAY_TRADE_PAGE_PAY);
        } else {
            requestParams.put("product_code", AliPayConstants.QUICK_WAP_PAY);
            aliPayRequest.setMethod(AliPayConstants.ALIPAY_TRADE_WAP_PAY);
        }
        requestParams.put("total_amount", String.valueOf(request.getOrderAmount()));
        requestParams.put("subject", String.valueOf(request.getOrderName()));
        requestParams.put("passback_params", request.getAttach());

        aliPayRequest.setAppId(aliPayConfig.getAppId());
        aliPayRequest.setCharset("utf-8");
        aliPayRequest.setSignType(AliPayConstants.SIGN_TYPE_RSA2);
        aliPayRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());
        // 优先使用PayRequest.returnUrl
        aliPayRequest.setReturnUrl(StringUtils.isEmpty(request.getReturnUrl()) ? aliPayConfig.getReturnUrl() : request.getReturnUrl());
        aliPayRequest.setTimestamp(LocalDateTime.now().format(formatter));
        aliPayRequest.setVersion("1.0");
        // 剔除空格、制表符、换行
        aliPayRequest.setBizContent(JsonUtil.toJson(requestParams).replaceAll("\\s*", ""));
        aliPayRequest.setSign(AliPaySignature.sign(MapUtil.object2MapWithUnderline(aliPayRequest), aliPayConfig.getPrivateKey()));

        Map<String, String> parameters = MapUtil.object2MapWithUnderline(aliPayRequest);
        Map<String, String> applicationParams = new HashMap<>();
        applicationParams.put("biz_content", aliPayRequest.getBizContent());
        parameters.remove("biz_content");
        String baseUrl = WebUtil.getRequestUrl(parameters, aliPayConfig.isSandbox());
        String body = WebUtil.buildForm(baseUrl, applicationParams);

        // pc 网站支付 只需返回body
        PayResponse response = new PayResponse();
        response.setBody(body);
        return response;
    }

    /**
     * 异步通知
     *
     * @param notifyData 通知数据
     * @return {@link AliPayResponse}
     */
    @Override
    public AliPayResponse asyncNotify(String notifyData) {
        try {
            notifyData = URLDecoder.decode(notifyData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 签名校验
        if (!AliPaySignature.verify(MapUtil.form2Map(notifyData), aliPayConfig.getAliPayPublicKey())) {
            log.error("【支付宝支付异步通知】签名验证失败, response={}", notifyData);
            throw new RuntimeException("【支付宝支付异步通知】签名验证失败");
        }
        HashMap<String, String> params = MapUtil.form2MapWithCamelCase(notifyData);
        AliPayAsyncResponse response = MapUtil.mapToObject(params, AliPayAsyncResponse.class);
        return buildPayResponse(response);
    }

    private AliPayResponse buildPayResponse(AliPayAsyncResponse response) {
        AliPayResponse aliPayResponse = new AliPayResponse();
        BeanUtils.copyProperties(response, aliPayResponse);
        return aliPayResponse;
    }
}
