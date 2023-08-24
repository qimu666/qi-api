package com.qimu.qiapibackend.model.dto.InterfaceOrder;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:22:17
 * @Version: 1.0
 * @Description: 创建请求
 */
@Data
public class InterfaceOrderAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 订单号
     */
    private String orderNo;
}