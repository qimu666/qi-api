package com.qimu.qiapibackend.service.alipay;

import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.service.ProductOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023年08月24日 22:19
 * @Version: 1.0
 * @Description:
 */
@SpringBootTest
public class AliPayTest {
    @Resource
    private BestPayServiceImpl bestPayService;

    @Resource
    private ProductOrderService productOrderService;

    @Test
    void pay() {
        List<ProductOrder> orderList = productOrderService.getNoPayOrderByDuration(15 * 24 * 60, true);
        boolean removeResult = productOrderService.removeBatchByIds(orderList);
    }
}
