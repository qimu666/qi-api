package com.qimu.qiapibackend.job;

import com.qimu.qiapibackend.model.entity.InterfaceOrder;
import com.qimu.qiapibackend.service.InterfaceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023年08月24日 09:24
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@Component
public class WxPayJob {
    @Resource
    private InterfaceOrderService interfaceOrderService;

    /**
     * 订单确认
     * 每30s查询一次超过5分钟过期的订单,并且未支付
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm() {
        List<InterfaceOrder> orderList = interfaceOrderService.getNoPayOrderByDuration(5);
        for (InterfaceOrder interfaceOrder : orderList) {
            String orderNo = interfaceOrder.getOrderNo();
            try {
                interfaceOrderService.checkOrderStatus(orderNo);
            } catch (Exception e) {
                log.error("超时订单,{},确认异常：{}", orderNo, e.getMessage());
            }
        }
    }

    /**
     * 订单确认
     * 每2点删除一次超过过期的订单,并且未支付，并且已关闭的订单
     */
    @Scheduled(cron = "* * 2 * * ?")
    public void clearOverdueOrders() {
        List<InterfaceOrder> orderList = interfaceOrderService.clearOverdueOrders();
        boolean removeResult = interfaceOrderService.removeBatchByIds(orderList);
        if (removeResult) {
            log.info("清除成功");
        }
    }
}
