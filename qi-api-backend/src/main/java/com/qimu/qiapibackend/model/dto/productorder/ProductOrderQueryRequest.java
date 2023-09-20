package com.qimu.qiapibackend.model.dto.productorder;

import com.qimu.qiapibackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:22:17
 * @Version: 1.0
 * @Description: 创建请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductOrderQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 商品名称
     */
    private String orderName;
    /**
     * 微信订单号/支付宝订单id
     */
    private String orderNo;
    /**
     * 金额(分)
     */
    private Integer total;

    /**
     * 接口订单状态(SUCCESS：支付成功
     * REFUND：转入退款
     * NOTPAY：未支付
     * CLOSED：已关闭
     * REVOKED：已撤销（仅付款码支付会返回）
     * USERPAYING：用户支付中（仅付款码支付会返回）
     * PAYERROR：支付失败（仅付款码支付会返回）)
     */
    private String status;

    /**
     * 支付方式（默认 WX- 微信 ZFB- 支付宝）
     */
    private String payType;
    /**
     * 商品信息
     */
    private String productInfo;
    /**
     * 增加积分个数
     */
    private Integer addPoints;
}