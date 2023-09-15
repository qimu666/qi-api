package com.qimu.qiapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023/08/31 07:59:34
 * @Version: 1.0
 * @Description: 调用请求
 */
@Data
public class InvokeRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private List<Field> requestParams;
    private String userRequestParams;

    @Data
    public static class Field {
        private String fieldName;
        private String value;
    }
}

