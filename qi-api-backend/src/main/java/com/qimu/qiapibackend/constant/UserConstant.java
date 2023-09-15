package com.qimu.qiapibackend.constant;

/**
 * 用户常量
 *
 * @author qimu
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";


    /**
     * 盐值，混淆密码
     */
    String SALT = "qimu";
    /**
     * ak/sk 混淆
     */
    String VOUCHER = "accessKey_secretKey";

}
