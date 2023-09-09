package com.qimu.qiapibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: QiMu
 * @Date: 2023/09/04 11:34:21
 * @Version: 1.0
 * @Description: 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String userName;

    private String checkPassword;

    private String invitationCode;

    private String agreeToAnAgreement;
}
