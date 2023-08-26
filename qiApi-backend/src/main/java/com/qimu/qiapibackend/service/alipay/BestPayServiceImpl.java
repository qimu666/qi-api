package com.qimu.qiapibackend.service.alipay;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;

import java.util.Objects;

/**
 * @Author: QiMu
 * @Date: 2023年08月26日 11:32
 * @Version: 1.0
 * @Description:
 */
public class BestPayServiceImpl extends com.lly835.bestpay.service.impl.BestPayServiceImpl {
    private AliPayConfig aliPayConfig;

    public AliPayConfig getAliPayConfig() {
        return aliPayConfig;
    }

    public void setAliPayConfig(AliPayConfig aliPayConfig) {
        this.aliPayConfig = aliPayConfig;
    }

    @Override
    public PayResponse pay(PayRequest request) {
        Objects.requireNonNull(request, "request params must not be null");
        // 支付宝支付
        if (BestPayPlatformEnum.ALIPAY == request.getPayTypeEnum().getPlatform()) {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(aliPayConfig);
            return aliPayService.pay(request);
        }
        throw new RuntimeException("错误的支付方式");
    }

    @Override
    public AliPayResponse asyncNotify(String notifyData) {
        AliPayServiceImpl aliPayService = new AliPayServiceImpl();
        aliPayService.setAliPayConfig(aliPayConfig);
        return aliPayService.asyncNotify(notifyData);
    }
}
