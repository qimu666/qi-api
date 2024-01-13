package com.qimu.qiapibackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qimu.qiapibackend.common.BaseResponse;
import com.qimu.qiapibackend.common.ErrorCode;
import com.qimu.qiapibackend.common.ResultUtils;
import com.qimu.qiapibackend.exception.BusinessException;
import com.qimu.qiapibackend.model.dto.pay.PayCreateRequest;
import com.qimu.qiapibackend.model.dto.productorder.ProductOrderQueryRequest;
import com.qimu.qiapibackend.model.entity.ProductInfo;
import com.qimu.qiapibackend.model.entity.ProductOrder;
import com.qimu.qiapibackend.model.enums.PaymentStatusEnum;
import com.qimu.qiapibackend.model.vo.OrderVo;
import com.qimu.qiapibackend.model.vo.ProductOrderVo;
import com.qimu.qiapibackend.model.vo.UserVO;
import com.qimu.qiapibackend.service.OrderService;
import com.qimu.qiapibackend.service.ProductOrderService;
import com.qimu.qiapibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public class OrderController {
    @Resource
    private UserService userService;
    @Resource
    private ProductOrderService productOrderService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate<String, Boolean> redisTemplate;

    // region 增删改查

    /**
     * 取消订单订单
     *
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/closed")
    public BaseResponse<Boolean> closedProductOrder(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        ProductOrder productOrder = productOrderService.getProductOrderByOutTradeNo(orderNo);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        ProductOrderService orderServiceByPayType = orderService.getProductOrderServiceByPayType(productOrder.getPayType());
        boolean closedResult = orderServiceByPayType.updateOrderStatusByOrderNo(orderNo, PaymentStatusEnum.CLOSED.getValue());
        return ResultUtils.success(closedResult);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteProductOrder(String id, HttpServletRequest request) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        // 校验数据是否存在
        ProductOrder productOrder = productOrderService.getById(id);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!productOrder.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(productOrderService.removeById(id));
    }

    /**
     * 按id获取产品订单
     *
     * @param id id
     * @return {@link BaseResponse}<{@link ProductOrderVo}>
     */
    @GetMapping("/get")
    public BaseResponse<ProductOrderVo> getProductOrderById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductOrder productOrder = productOrderService.getById(id);
        if (productOrder == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        ProductOrderVo productOrderVo = formatProductOrderVo(productOrder);
        return ResultUtils.success(productOrderVo);
    }

    /**
     * 分页获取列表
     *
     * @param productOrderQueryRequest 接口信息查询请求
     * @param request                  请求
     * @return {@link BaseResponse}<{@link Page}<{@link com.qimu.qiapibackend.model.entity.ProductOrder}>>
     */
    @GetMapping("/list/page")
    public BaseResponse<OrderVo> listProductOrderByPage(ProductOrderQueryRequest productOrderQueryRequest, HttpServletRequest request) {
        if (productOrderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        com.qimu.qiapibackend.model.entity.ProductOrder productOrder = new com.qimu.qiapibackend.model.entity.ProductOrder();
        BeanUtils.copyProperties(productOrderQueryRequest, productOrder);
        long size = productOrderQueryRequest.getPageSize();
        String orderName = productOrderQueryRequest.getOrderName();
        String orderNo = productOrderQueryRequest.getOrderNo();
        Integer total = productOrderQueryRequest.getTotal();
        String status = productOrderQueryRequest.getStatus();
        String productInfo = productOrderQueryRequest.getProductInfo();
        String payType = productOrderQueryRequest.getPayType();
        Integer addPoints = productOrderQueryRequest.getAddPoints();
        long current = productOrderQueryRequest.getCurrent();

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<ProductOrder> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(orderName), "orderName", orderName)
                .like(StringUtils.isNotBlank(productInfo), "productInfo", productInfo)
                .eq("userId", userId)
                .eq(StringUtils.isNotBlank(orderNo), "orderNo", orderNo)
                .eq(StringUtils.isNotBlank(status), "status", status)
                .eq(StringUtils.isNotBlank(payType), "payType", payType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 未支付的订单前置
        queryWrapper.last("ORDER BY CASE WHEN status = 'NOTPAY' THEN 0 ELSE 1 END, status");
        Page<ProductOrder> productOrderPage = productOrderService.page(new Page<>(current, size), queryWrapper);
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(productOrderPage, orderVo);
        // 处理订单信息,
        List<ProductOrderVo> productOrders = productOrderPage.getRecords().stream().map(this::formatProductOrderVo).collect(Collectors.toList());
        orderVo.setRecords(productOrders);
        return ResultUtils.success(orderVo);
    }
    // endregion

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
        UserVO loginUser = userService.getLoginUser(request);
        ProductOrderVo productOrderVo = orderService.createOrderByPayType(productId, payType, loginUser);
        if (productOrderVo == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单创建失败，请稍后再试");
        }
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

    @PostMapping("/notify/order")
    public String parseOrderNotifyResult(@RequestBody String notifyData, HttpServletRequest request) {
        return orderService.doOrderNotify(notifyData, request);
    }

    private ProductOrderVo formatProductOrderVo(ProductOrder productOrder) {
        ProductOrderVo productOrderVo = new ProductOrderVo();
        BeanUtils.copyProperties(productOrder, productOrderVo);
        ProductInfo prodInfo = JSONUtil.toBean(productOrder.getProductInfo(), ProductInfo.class);
        productOrderVo.setDescription(prodInfo.getDescription());
        productOrderVo.setProductType(prodInfo.getProductType());
        String voTotal = String.valueOf(prodInfo.getTotal());
        BigDecimal total = new BigDecimal(voTotal).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        productOrderVo.setTotal(total.toString());
        return productOrderVo;
    }
}
