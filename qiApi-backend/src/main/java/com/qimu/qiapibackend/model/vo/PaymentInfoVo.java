package com.qimu.qiapibackend.model.vo;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import lombok.Data;

import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023年08月24日 10:33
 * @Version: 1.0
 * @Description:
 */
@Data
public class PaymentInfoVo {
    private static final long serialVersionUID = 1L;

    private String appid;

    private String mchid;

    private String outTradeNo;

    private String transactionId;

    /**
     * 贸易类型
     */
    private String tradeType;

    private String tradeState;

    private String tradeStateDesc;

    private String bankType;

    private String attach;

    private String successTime;

    private WxPayOrderNotifyV3Result.Payer payer;

    private WxPayOrderNotifyV3Result.Amount amount;

    private WxPayOrderNotifyV3Result.SceneInfo sceneInfo;

    private List<WxPayOrderNotifyV3Result.PromotionDetail> promotionDetails;
}
