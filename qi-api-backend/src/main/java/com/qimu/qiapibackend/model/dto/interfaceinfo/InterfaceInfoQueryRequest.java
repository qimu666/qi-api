package com.qimu.qiapibackend.model.dto.interfaceinfo;

import com.qimu.qiapibackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:33:30
 * @Version: 1.0
 * @Description: 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

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
     * 发布人
     */
    private Long userId;
    /**
     * 减少积分个数
     */
    private Integer reduceScore;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 接口状态（0- 默认下线 1- 上线）
     */
    private Integer status;
}