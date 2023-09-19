package com.qimu.qiapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023/08/25 02:50:48
 * @Version: 1.0
 * @Description: 更新请求
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private long id;
    /**
     * 接口名称
     */
    private String name;
    /**
     * 返回格式
     */
    private String returnFormat;
    /**
     * 接口响应参数
     */
    private List<ResponseParamsField> responseParams;
    /**
     * 接口地址
     */
    private String url;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 调用接口扣费个数
     */
    private Integer reduceScore;
    /**
     * 接口头像
     */
    private String avatarUrl;

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

    /**
     * 接口请求参数
     */
    private List<RequestParamsField> requestParams;
    /**
     * 接口状态（0- 默认下线 1- 上线）
     */
    private Integer status;
}