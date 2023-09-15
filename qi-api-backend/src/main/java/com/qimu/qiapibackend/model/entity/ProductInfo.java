package com.qimu.qiapibackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: QiMu
 * @Date: 2023/08/25 02:54:05
 * @Version: 1.0
 * @Description: 产品信息
 */
@TableName(value = "product_info")
@Data
public class ProductInfo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 状态（0- 默认下线 1- 上线）
     */
    private Integer status;
    /**
     * 增加积分个数
     */
    private Integer addPoints;
    /**
     * 产品描述
     */
    private String description;
    /**
     * 创建人
     */
    private Long userId;
    /**
     * 金额(分)
     */
    private Integer total;
    /**
     * 产品类型（VIP-会员 RECHARGE-充值）
     */
    private String productType;
    /**
     * 过期时间
     */
    private Date expirationTime;
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