package com.qimu.qiapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.enums.PaymentStatusEnum;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;
import com.qimu.qiapibackend.service.OrderService;
import com.qimu.qiapibackend.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.ALIPAY;
import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.WX;

/**
 * @Author: QiMu
 * @Date: 2023/08/25 06:22:02
 * @Version: 1.0
 * @Description: 订单服务
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private List<ProductOrderService> productOrderServices;

    /**
     * 按付费类型获取产品订单服务
     *
     * @param payType 付款类型
     * @return {@link ProductOrderService}
     */
    @Override
    public ProductOrderService getProductOrderServiceByPayType(String payType) {
        return productOrderServices.stream()
                .filter(s -> {
                    Qualifier qualifierAnnotation = s.getClass().getAnnotation(Qualifier.class);
                    return qualifierAnnotation != null && qualifierAnnotation.value().equals(payType);
                })
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该支付方式"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductOrderVo createOrderByPayType(Long productId, String payType, User loginUser) {
        // 按付费类型获取产品订单服务Bean
        ProductOrderService productOrderService = getProductOrderServiceByPayType(payType);
        // 创建支付订单
        ProductOrderVo productOrderVo = productOrderService.getProductOrder(productId, loginUser, payType);
        // 订单存在就返回不再新创建
        if (productOrderVo != null) {
            return productOrderVo;
        }
        // 保存订单,返回vo信息
        return productOrderService.saveProductOrder(productId, loginUser);

    }

    /**
     * 查找超过minutes分钟并且未支付的的订单
     *
     * @param minutes 分钟
     * @return {@link List}<{@link ProductOrder}>
     */
    @Override
    public List<ProductOrder> getNoPayOrderByDuration(int minutes, Boolean remove, String payType) {
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
        LambdaQueryWrapper<ProductOrder> productOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productOrderLambdaQueryWrapper.eq(ProductOrder::getStatus, PaymentStatusEnum.NOTPAY.getValue());
        if (StringUtils.isNotBlank(payType)) {
            productOrderLambdaQueryWrapper.eq(ProductOrder::getPayType, payType);
        }
        // 大于5分钟表示删除
        if (remove) {
            productOrderLambdaQueryWrapper.or().eq(ProductOrder::getStatus, PaymentStatusEnum.CLOSED.getValue());
        }
        productOrderLambdaQueryWrapper.and(p -> p.le(ProductOrder::getCreateTime, instant));
        return productOrderService.list(productOrderLambdaQueryWrapper);
    }

    /**
     * 做订单通知
     * 支票支付类型
     *
     * @param notifyData 通知数据
     * @param request    要求
     * @return {@link String}
     */
    @Override
    public String doOrderNotify(String notifyData, HttpServletRequest request) {
        String payType;
        if (notifyData.startsWith("gmt_create=") && notifyData.contains("gmt_create") && notifyData.contains("sign_type") && notifyData.contains("notify_type")) {
            payType = ALIPAY.getValue();
        } else {
            payType = WX.getValue();
        }
        return this.getProductOrderServiceByPayType(payType).doPaymentNotify(notifyData, request);
    }
}
