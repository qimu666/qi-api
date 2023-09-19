package com.qimu.qiapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:33:26
 * @Version: 1.0
 * @Description: 创建请求
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 接口名称
     */
    private String name;
    /**
     * 返回格式
     */
    private String returnFormat;
    /**
     * 接口地址
     */
    private String url;
    /**
     * 接口响应参数
     */
    private List<ResponseParamsField> responseParams;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 减少积分个数
     */
    private Integer reduceScore;
    /**
     * 接口请求参数
     */
    private List<RequestParamsField> requestParams;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 请求示例
     */
    private String requestExample;
    /**
     * 请求头
     */
    private String requestHeader;
    /**
     * 响应头
     */
    private String responseHeader;


}