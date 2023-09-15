package com.qimu.qiapibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:34:24
 * @Version: 1.0
 * @Description: 用户更新请求
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 性别
     */
    private String gender;
    /**
     * 用户角色: user, admin
     */
    private String userRole;
    /**
     * 账号状态（0- 正常 1- 封号）
     */
    private Integer status;
    /**
     * 密码
     */
    private String userPassword;

    /**
     * 钱包余额（分）
     */
    private Integer balance;
}