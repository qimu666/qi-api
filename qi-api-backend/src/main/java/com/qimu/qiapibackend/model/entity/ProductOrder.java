package com.qimu.qiapibackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:31:48
 * @Version: 1.0
 * @Description: 商品订单
 */
@TableName(value = "product_order")
@Data
public class ProductOrder implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 微信订单号/支付宝订单id
     */
    private String orderNo;

    /**
     * 支付方式（默认 WX- 微信 ZFB- 支付宝）
     */
    private String payType;

    /**
     * 过期时间
     */
    private Date expirationTime;

    /**
     * 商品名称
     */
    private String orderName;

    /**
     * 二维码地址
     */
    private String codeUrl;

    /**
     * 商品信息
     */
    private String productInfo;

    /**
     * 金额(分)
     */
    private Integer total;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 创建人
     */
    private Long userId;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 增加积分个数
     */
    private Integer addPoints;
    /**
     * 支付宝订单体
     */
    private String formData;
}