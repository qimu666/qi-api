package com.qimu.qiapibackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qimu.qiapibackend.model.vo.UserVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 *
 * @TableName interface_info
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
     * 接口名称
     */
    private String name;
    /**
     * 接口地址
     */
    private String url;

    /**
     * 金额(分),调用接口扣费金额
     */
    private Integer total;
    /**
     * 发布人
     */
    private Long userId;
    /**
     * 请求方法
     */
    private String method;
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

    @TableField(exist = false)
    private UserVO userVO;
}