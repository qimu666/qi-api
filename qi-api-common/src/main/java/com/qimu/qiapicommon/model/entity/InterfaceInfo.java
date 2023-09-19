package com.qimu.qiapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:31:33
 * @Version: 1.0
 * @Description: 接口信息
 */
@TableName(value = "interface_info")
@Data
public class InterfaceInfo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 返回格式
     */
    private String returnFormat;
    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口地址
     */
    private String url;

    /**
     * 发布人
     */
    private Long userId;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 总调用次数
     */
    private Long totalInvokes;
    /**
     * 接口请求参数
     */
    private String requestParams;
    /**
     * 接口响应参数
     */
    private String responseParams;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 请求示例
     */
    private String requestExample;
    /**
     * 减少积分个数
     */
    private Integer reduceScore;
    /**
     * 接口头像
     */
    private String avatarUrl;
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
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}