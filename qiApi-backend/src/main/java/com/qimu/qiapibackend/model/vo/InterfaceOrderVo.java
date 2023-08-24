package com.qimu.qiapibackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: QiMu
 * @Date: 2023年08月23日 18:02
 * @Version: 1.0
 * @Description:
 */
@Data
public class InterfaceOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 支付类型
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
     * 支付二维码地址
     */
    private String codeUrl;
    /**
     * 金额(分)
     */
    private int total;
    /**
     * 接口状态（0- 未支付 1- 已支付）
     */
    private String status;
}
