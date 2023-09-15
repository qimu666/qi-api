package com.qimu.qiapibackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: QiMu
 * @Date: 2023/08/31 11:48:45
 * @Version: 1.0
 * @Description: 每日签到表
 */
@TableName(value = "daily_check_in")
@Data
public class DailyCheckIn implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 签到人
     */
    private Long userId;
    /**
     * 描述
     */
    private String description;
    /**
     * 签到增加积分个数
     */
    private Integer addPoints;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}