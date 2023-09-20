package com.qimu.qiapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;
import com.qimu.qiapibackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:12:50
 * @Version: 1.0
 * @Description: 接口订单服务
 */
public interface ProductOrderService extends IService<ProductOrder> {

    /**
     * 保存产品订单
     *
     * @param productId 产品id
     * @param loginUser 登录用户
     * @return {@link ProductOrderVo}
     */
    ProductOrderVo saveProductOrder(Long productId, UserVO loginUser);

    /**
     * 更新产品订单
     *
     * @param productOrder 产品订单
     * @return boolean
     */
    boolean updateProductOrder(ProductOrder productOrder);

    /**
     * 获取产品订单
     * 获取订单
     *
     * @param productId 产品id
     * @param loginUser 登录用户
     * @param payType   付款类型
     * @return {@link ProductOrderVo}
     */
    ProductOrderVo getProductOrder(Long productId, UserVO loginUser, String payType);


    /**
     * 按订单号更新订单状态
     *
     * @param outTradeNo  订单号
     * @param orderStatus 订单状态
     * @return boolean
     */
    boolean updateOrderStatusByOrderNo(String outTradeNo, String orderStatus);

    /**
     * 按订单号关闭订单
     *
     * @param outTradeNo 外贸编号
     * @throws Exception 例外
     */
    void closedOrderByOrderNo(String outTradeNo) throws Exception;


    /**
     * 通过out trade no获得产品订单
     * 获取产品订单状态
     *
     * @param outTradeNo 外贸编号
     * @return {@link String}
     */
    ProductOrder getProductOrderByOutTradeNo(String outTradeNo);

    /**
     * 处理超时订单
     * 检查订单状态(微信查单接口)
     *
     * @param productOrder 产品订单
     */
    void processingTimedOutOrders(ProductOrder productOrder);


    /**
     * 付款通知
     * 处理付款通知
     *
     * @param notifyData 通知数据
     * @param request    要求
     * @return {@link String}
     */
    String doPaymentNotify(String notifyData, HttpServletRequest request);
}
