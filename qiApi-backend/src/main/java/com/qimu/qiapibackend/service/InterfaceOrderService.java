package com.qimu.qiapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.qimu.qiapibackend.model.entity.InterfaceOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.vo.InterfaceOrderVo;

import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:12:50
 * @Version: 1.0
 * @Description: 接口订单服务
 */
public interface InterfaceOrderService extends IService<InterfaceOrder> {

    /**
     * 获取订单
     *
     * @param interfaceId 接口id
     * @param loginUser   登录用户
     * @return {@link InterfaceOrderVo}
     */
    InterfaceOrderVo getInterfaceOrder(Long interfaceId, User loginUser);

    /**
     * 保存接口顺序
     * 保存接口订单
     *
     * @param interfaceId 接口id
     * @param loginUser   登录用户
     * @param orderNo     订单号
     * @return {@link InterfaceOrder}
     */
    InterfaceOrder saveInterfaceOrder(Long interfaceId, String orderNo, User loginUser);

    /**
     * 更新接口订单
     *
     * @param codeUrl url代码
     * @param id      id
     * @return boolean
     */
    boolean updateInterfaceOrder(String codeUrl, Long id);

    /**
     * 根据订单号更新订单状态
     *
     * @param outTradeNo 订单号
     */
    void updateOrderStatusByOrderNo(String outTradeNo, String orderStatus);


    /**
     * 获取接口订单状态
     *
     * @param outTradeNo 外贸编号
     * @return {@link String}
     */
    String getInterfaceOrderStatus(String outTradeNo);

    /**
     * 按持续时间获得未支付订单
     *
     * @param minutes 分钟
     * @return {@link List}<{@link InterfaceOrder}>
     */
    List<InterfaceOrder> getNoPayOrderByDuration(int minutes);

    /**
     * 检查订单状态
     * 检查订单状态(微信查单接口)
     *
     * @param orderNo 订单号
     * @throws WxPayException wx支付例外
     */
    void checkOrderStatus(String orderNo) throws WxPayException;

    /**
     * 已过期的订单
     *
     * @return {@link List}<{@link InterfaceOrder}>
     */
    List<InterfaceOrder> clearOverdueOrders();
}
