package com.qimu.qiapibackend.constant;

/**
 * @Author: QiMu
 * @Date: 2023/08/24 11:37:12
 * @Version: 1.0
 * @Description: 支付常数
 */
public interface PayConstant {
    /**
     * 查询订单状态Key
     */
    String QUERY_ORDER_STATUS = "query:orderStatus:";

    /**
     * 订单前缀
     */
    String ORDER_PREFIX = "order_";

    /**
     * 查询订单信息
     */
    String QUERY_ORDER_INFO = "query:orderInfo:";

    /**
     * 支付宝响应代码表示成功
     */
    String RESPONSE_CODE_SUCCESS = "10000";

    /**
     * 商户签约的产品支持退款功能的前提下，买家付款成功；
     */
    String TRADE_SUCCESS = "TRADE_SUCCESS";
}
