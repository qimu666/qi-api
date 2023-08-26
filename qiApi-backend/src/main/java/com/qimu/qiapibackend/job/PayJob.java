package com.qimu.qiapibackend.job;

import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.WX;

/**
 * @Author: QiMu
 * @Date: 2023年08月24日 09:24
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@Component
public class PayJob {
    @Resource
    @Qualifier("WX")
    private ProductOrderService productOrderService;

    /**
     * 订单确认
     * 每30s查询一次超过5分钟过期的订单,并且未支付
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm() {
        List<ProductOrder> orderList = productOrderService.getNoPayOrderByDuration(5, false);
        for (ProductOrder productOrder : orderList) {
            String orderNo = productOrder.getOrderNo();
            String payType = productOrder.getPayType();
            try {
                if (payType.equals(WX.getValue())) {
                    productOrderService.checkOrderStatus(productOrder);
                } else {
                    break;
                }
            } catch (Exception e) {
                log.error("超时订单,{},确认异常：{}", orderNo, e.getMessage());
            }
        }
    }

    /**
     * 订单确认
     * 每2点删除一次15天前的订单,并且未支付，并且已关闭的订单
     */
    @Scheduled(cron = "* * 2 * * ?")
    public void clearOverdueOrders() {
        List<ProductOrder> orderList = productOrderService.getNoPayOrderByDuration(15 * 24 * 60, true);
        boolean removeResult = productOrderService.removeBatchByIds(orderList);
        if (removeResult) {
            log.info("清除成功");
        }
    }
}
