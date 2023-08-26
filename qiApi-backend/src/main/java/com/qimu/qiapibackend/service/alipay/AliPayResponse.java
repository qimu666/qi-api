package com.qimu.qiapibackend.service.alipay;

import com.lly835.bestpay.model.PayResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/08/26 02:08:26
 * @Version: 1.0
 * @Description: 支付时的同步/异步返回参数
 */

@Data
public class AliPayResponse extends PayResponse implements Serializable {
    private static final long serialVersionUID = -1744947319936459548L;
    /**
     * 卖家id
     */
    private String sellerId;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 商品名称
     */
    private String subject;

    /**
     * 总额
     */
    private String totalAmount;

    /**
     * 收据金额
     */
    private String receiptAmount;

    /**
     * 返回参数
     */
    private String passbackParams;

    /**
     * 买方id
     */
    private String buyerId;

    /**
     * 订单编号
     */
    private String tradeNo;
}