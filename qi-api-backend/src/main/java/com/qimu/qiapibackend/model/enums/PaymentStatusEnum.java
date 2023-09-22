package com.qimu.qiapibackend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: QiMu
 * @Date: 2023/08/24 10:12:14
 * @Version: 1.0
 * @Description: 付款状态枚举
 */
public enum PaymentStatusEnum {

    /**
     * 支付成功
     */
    SUCCESS("支付成功", "SUCCESS"),

    /**
     * 支付失败
     */
    PAY_ERROR("支付失败", "PAYERROR"),
    /**
     * 用户付费中
     */
    USER_PAYING("用户支付中", "USER_PAYING"),
    /**
     * 已关闭
     */
    CLOSED("已关闭", "CLOSED"),

    /**
     * 未支付
     */
    NOTPAY("未支付", "NOTPAY"),
    /**
     * 转入退款
     */
    REFUND("转入退款", "REFUND"),
    /**
     * 退款中
     */
    PROCESSING("退款中", "PROCESSING"),
    /**
     * 撤销
     */
    REVOKED("已撤销（刷卡支付）", "REVOKED"),

    /**
     * 未知
     */
    UNKNOW("未知状态", "UNKNOW");


    private final String text;

    private final String value;

    PaymentStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值
     * 得到值
     * 获取值列表
     *
     * @return {@link List}<{@link String}>
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
