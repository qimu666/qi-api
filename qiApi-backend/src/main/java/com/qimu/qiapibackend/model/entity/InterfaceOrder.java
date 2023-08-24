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
 * @Date: 2023/08/24 11:20:58
 * @Version: 1.0
 * @Description: 接口订单
 */
@TableName(value = "interface_order")
@Data
public class InterfaceOrder implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 二维码地址
     */
    private String codeUrl;
    /**
     * 创建人
     */
    private Long userId;
    /**
     * 接口id
     */
    private Long interfaceId;
    /**
     * 商品名称
     */
    private String orderName;
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
     * 过期时间
     */
    private Date expirationTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}