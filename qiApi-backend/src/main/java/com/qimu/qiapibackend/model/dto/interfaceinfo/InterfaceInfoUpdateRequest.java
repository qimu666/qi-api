package com.qimu.qiapibackend.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

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
     * 接口地址
     */
    private String url;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 金额(分),调用接口扣费金额
     */
    private Integer total;
    /**
     * 接口请求参数
     */
    private String requestParams;
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
     * 接口状态（0- 默认下线 1- 上线）
     */
    private Integer status;
}