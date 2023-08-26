package com.qimu.qiapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    ProductOrderVo saveProductOrder(Long productId, User loginUser);

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
    ProductOrderVo getProductOrder(Long productId, User loginUser, String payType);


    /**
     * 按订单号更新订单状态
     *
     * @param outTradeNo  订单号
     * @param orderStatus 订单状态
     * @return
     */
    boolean updateOrderStatusByOrderNo(String outTradeNo, String orderStatus);


    /**
     * 通过out trade no获得产品订单
     * 获取产品订单状态
     *
     * @param outTradeNo 外贸编号
     * @return {@link String}
     */
    ProductOrder getProductOrderByOutTradeNo(String outTradeNo);

    /**
     * 按持续时间获得未支付订单
     *
     * @param minutes 分钟
     * @param remove  是否是删除
     * @return {@link List}<{@link ProductOrder}>
     */
    List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove);

    /**
     * 检查订单状态
     * 检查订单状态(微信查单接口)
     *
     * @param productOrder 产品订单
     * @throws WxPayException wx支付例外
     */
    void checkOrderStatus(ProductOrder productOrder) throws WxPayException;


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
