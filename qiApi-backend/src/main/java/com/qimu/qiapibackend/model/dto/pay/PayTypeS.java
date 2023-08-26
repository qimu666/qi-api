package com.qimu.qiapibackend.model.dto.pay;

import com.qimu.qiapibackend.service.ProductOrderService;
import com.qimu.qiapibackend.service.impl.AlipayOrderServiceImpl;
import com.qimu.qiapibackend.service.impl.WxOrderServiceImpl;

import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.WX;

/**
 * @Author: QiMu
 * @Date: 2023年08月25日 19:42
 * @Version: 1.0
 * @Description:
 */
public class PayTypeS {
    public static ProductOrderService productOrderService(String type) {
        if (type.equals(WX.getValue())) {
            return new WxOrderServiceImpl();
        } else {
            return new AlipayOrderServiceImpl();
        }
    }
}
