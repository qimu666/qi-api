package com.qimu.qiapicommon.model.emums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:49:37
 * @Version: 1.0
 * @Description: 用户帐户状态枚举
 */
public enum UserAccountStatusEnum {

    /**
     * 正常
     */
    NORMAL("正常", 0),
    /**
     * 封号
     */
    BAN("封禁", 1);

    private final String text;

    private final int value;

    UserAccountStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值
     * 获取值列表
     *
     * @return {@link List}<{@link Integer}>
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
