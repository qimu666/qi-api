package com.qimu.qiapiinterface.utils;

import icu.qimuu.qiapisdk.exception.BusinessException;
import icu.qimuu.qiapisdk.exception.ErrorCode;

import java.lang.reflect.Field;

/**
 * @Author: QiMu
 * @Date: 2023年09月19日 11:12
 * @Version: 1.0
 * @Description:
 */
public class UrlUtils {
    public static <T> String buildUrl(String baseUrl, T params) throws BusinessException {
        StringBuilder url = new StringBuilder(baseUrl);
        Field[] fields = params.getClass().getDeclaredFields();
        boolean isFirstParam = true;
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            // 跳过serialVersionUID属性
            if ("serialVersionUID".equals(name)) {
                continue;
            }
            try {
                Object value = field.get(params);
                if (value != null) {
                    if (isFirstParam) {
                        url.append("?").append(name).append("=").append(value);
                        isFirstParam = false;
                    } else {
                        url.append("&").append(name).append("=").append(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "构建url异常");
            }
        }
        return url.toString();
    }
}
