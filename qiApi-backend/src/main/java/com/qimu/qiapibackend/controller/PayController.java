package com.qimu.qiapibackend.controller;

import com.qimu.qiapibackend.common.BaseResponse;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.common.ResultUtils;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.model.dto.ProductOrder.ProductOrderQueryRequest;
import com.qimu.qiapibackend.model.dto.pay.PayCreateRequest;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;
import com.qimu.qiapibackend.service.OrderService;
import com.qimu.qiapibackend.service.ProductOrderService;
import com.qimu.qiapibackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static com.qimu.qiapibackend.constant.PayConstant.QUERY_ORDER_STATUS;
import static com.qimu.qiapibackend.model.enums.PaymentStatusEnum.SUCCESS;


/**
 * @Author: QiMu
 * @Date: 2023年08月23日 00:13
 * @Version: 1.0
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class PayController {
    @Resource
    private UserService userService;
    @Resource
    private ProductOrderService productOrderService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    /**
     * 创建订单
     *
     * @param request          要求
     * @param payCreateRequest 付款创建请求
     * @return {@link BaseResponse}<{@link ProductOrderVo}>
     */
    @PostMapping("/create")
    public BaseResponse<ProductOrderVo> createOrder(@RequestBody PayCreateRequest payCreateRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(payCreateRequest) || StringUtils.isBlank(payCreateRequest.getProductId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long productId = Long.valueOf(payCreateRequest.getProductId());
        String payType = payCreateRequest.getPayType();
        if (StringUtils.isBlank(payType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂无该支付方式");
        }
        User loginUser = userService.getLoginUser(request);
        ProductOrderVo productOrderVo = orderService.createOrderByPayType(productId, payType, loginUser);
        return ResultUtils.success(productOrderVo);
    }


    /**
     * 查询订单状态
     *
     * @param productOrderQueryRequest 接口订单查询请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/query/status")
    public BaseResponse<Boolean> queryOrderStatus(@RequestBody ProductOrderQueryRequest productOrderQueryRequest) {
        if (ObjectUtils.isEmpty(productOrderQueryRequest) || StringUtils.isBlank(productOrderQueryRequest.getOrderNo())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String orderNo = productOrderQueryRequest.getOrderNo();
        Boolean data = redisTemplate.opsForValue().get(QUERY_ORDER_STATUS + orderNo);
        if (Boolean.FALSE.equals(data)) {
            return ResultUtils.success(data);
        }
        ProductOrder productOrder = productOrderService.getProductOrderByOutTradeNo(orderNo);
        if (SUCCESS.getValue().equals(productOrder.getStatus())) {
            return ResultUtils.success(true);
        }
        redisTemplate.opsForValue().set(QUERY_ORDER_STATUS + orderNo, false, 5, TimeUnit.MINUTES);
        return ResultUtils.success(false);
    }

    /**
     * 解析订单通知结果
     * 通知频率为15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h - 总计 24h4m
     *
     * @param notifyData 通知数据
     * @param request    请求
     * @return {@link String}
     */

    @ApiOperation("支付回调通知处理")
    @PostMapping("/notify/order")
    public String parseOrderNotifyResult(@RequestBody String notifyData, HttpServletRequest request) {
        return orderService.doOrderNotify(notifyData, request);
    }
}
