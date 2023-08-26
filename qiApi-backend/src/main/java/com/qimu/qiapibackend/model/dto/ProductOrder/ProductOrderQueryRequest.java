package com.qimu.qiapibackend.model.dto.ProductOrder;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:22:17
 * @Version: 1.0
 * @Description: 创建请求
 */
@Data
public class ProductOrderQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;
}