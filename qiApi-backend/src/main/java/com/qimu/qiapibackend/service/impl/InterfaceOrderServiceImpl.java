package com.qimu.qiapibackend.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.mapper.InterfaceOrderMapper;
import com.qimu.qiapibackend.model.entity.InterfaceInfo;
import com.qimu.qiapibackend.model.entity.InterfaceOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.vo.InterfaceOrderVo;
import com.qimu.qiapibackend.model.vo.PaymentInfoVo;
import com.qimu.qiapibackend.service.InterfaceInfoService;
import com.qimu.qiapibackend.service.InterfaceOrderService;
import com.qimu.qiapibackend.service.PaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.qimu.qiapibackend.constant.PayConstant.QUERY_ORDER_STATUS;
import static com.qimu.qiapibackend.model.enums.PayTypeStatusEnum.WX;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.*;


/**
 * @Author: QiMu
 * @Date: 2023/08/23 03:18:35
 * @Version: 1.0
 * @Description: 接口顺序服务impl
 */
@Service
@Slf4j
public class InterfaceOrderServiceImpl extends ServiceImpl<InterfaceOrderMapper, InterfaceOrder> implements InterfaceOrderService {

    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private WxPayService wxPayService;

    @Resource
    private PaymentInfoService paymentInfoService;

    @Override
    public InterfaceOrderVo getInterfaceOrder(Long interfaceId, User loginUser) {

        LambdaQueryWrapper<InterfaceOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceOrder::getInterfaceId, interfaceId);
        lambdaQueryWrapper.eq(InterfaceOrder::getStatus, NOTPAY.getValue());
        lambdaQueryWrapper.eq(InterfaceOrder::getUserId, loginUser.getId());
        lambdaQueryWrapper.gt(InterfaceOrder::getExpirationTime, DateUtil.date(System.currentTimeMillis()));

        InterfaceOrder oldOrder = this.getOne(lambdaQueryWrapper);
        if (oldOrder == null) {
            return null;
        }
        InterfaceOrderVo interfaceOrderVo = new InterfaceOrderVo();
        BeanUtils.copyProperties(oldOrder, interfaceOrderVo);
        return interfaceOrderVo;
    }

    @Override
    public InterfaceOrder saveInterfaceOrder(Long interfaceId, String orderNo, User loginUser) {

        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        Date date = DateUtil.date(System.currentTimeMillis());
        Date offset = DateUtil.offset(date, DateField.MINUTE, 5);

        InterfaceOrder interfaceOrder = new InterfaceOrder();
        interfaceOrder.setUserId(loginUser.getId());
        interfaceOrder.setOrderNo(orderNo);
        interfaceOrder.setInterfaceId(interfaceInfo.getId());
        interfaceOrder.setOrderName(interfaceInfo.getName());
        interfaceOrder.setTotal(9);
        interfaceOrder.setStatus(NOTPAY.getValue());
        interfaceOrder.setPayType(WX.getValue());
        interfaceOrder.setExpirationTime(offset);

        boolean result = this.save(interfaceOrder);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return interfaceOrder;
    }

    @Override
    public boolean updateInterfaceOrder(String codeUrl, Long id) {
        InterfaceOrder updateCodeUrl = new InterfaceOrder();
        updateCodeUrl.setCodeUrl(codeUrl);
        updateCodeUrl.setId(id);
        return this.updateById(updateCodeUrl);
    }

    @Override
    public void updateOrderStatusByOrderNo(String outTradeNo, String orderStatus) {
        InterfaceOrder interfaceOrder = new InterfaceOrder();
        interfaceOrder.setStatus(orderStatus);
        LambdaQueryWrapper<InterfaceOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceOrder::getOrderNo, outTradeNo);
        this.update(interfaceOrder, lambdaQueryWrapper);
    }

    @Override
    public String getInterfaceOrderStatus(String outTradeNo) {
        LambdaQueryWrapper<InterfaceOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceOrder::getOrderNo, outTradeNo);
        InterfaceOrder interfaceOrder = this.getOne(lambdaQueryWrapper);
        if (interfaceOrder == null) {
            return null;
        }
        return interfaceOrder.getStatus();
    }

    /**
     * 查找超过minutes分钟并且未支付的的订单
     *
     * @param minutes 分钟
     * @return {@link List}<{@link InterfaceOrder}>
     */
    @Override
    public List<InterfaceOrder> getNoPayOrderByDuration(int minutes) {
        Instant instant = Instant.now().minus(Duration.ofMinutes(minutes));
        LambdaQueryWrapper<InterfaceOrder> interfaceOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        interfaceOrderLambdaQueryWrapper.eq(InterfaceOrder::getStatus, NOTPAY.getValue());
        interfaceOrderLambdaQueryWrapper.le(InterfaceOrder::getCreateTime, instant);
        return this.list(interfaceOrderLambdaQueryWrapper);
    }

    @Override
    public void checkOrderStatus(String orderNo) throws WxPayException {
        WxPayOrderQueryV3Request wxPayOrderQueryV3Request = new WxPayOrderQueryV3Request();
        wxPayOrderQueryV3Request.setOutTradeNo(orderNo);
        WxPayOrderQueryV3Result wxPayOrderQueryV3Result = wxPayService.queryOrderV3(wxPayOrderQueryV3Request);

        String tradeState = wxPayOrderQueryV3Result.getTradeState();
        // 订单已支付
        if (tradeState.equals(SUCCESS.getValue())) {
            this.updateOrderStatusByOrderNo(orderNo, SUCCESS.getValue());
            PaymentInfoVo paymentInfoVo = new PaymentInfoVo();
            BeanUtils.copyProperties(wxPayOrderQueryV3Result, paymentInfoVo);
            paymentInfoService.createPaymentInfo(paymentInfoVo);
            log.info("超时订单{},状态已更新", orderNo);
        }
        if (tradeState.equals(NOTPAY.getValue())) {
            wxPayService.closeOrderV3(orderNo);
            this.updateOrderStatusByOrderNo(orderNo, CLOSED.getValue());
            log.info("超时订单{},订单已关闭", orderNo);
        }
        redisTemplate.delete(QUERY_ORDER_STATUS + orderNo);
    }

    @Override
    public List<InterfaceOrder> clearOverdueOrders() {
        Instant instant = Instant.now().minus(Duration.ofMinutes(5));
        LambdaQueryWrapper<InterfaceOrder> interfaceOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        interfaceOrderLambdaQueryWrapper
                .eq(InterfaceOrder::getStatus, NOTPAY.getValue()).or()
                .eq(InterfaceOrder::getStatus, CLOSED.getValue())
                .and(q -> q.le(InterfaceOrder::getCreateTime, instant));
        return this.list(interfaceOrderLambdaQueryWrapper);
    }
}




