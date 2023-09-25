package com.qimu.qiapibackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qimu.qiapibackend.model.dto.user.*;
import com.qimu.qiapibackend.model.entity.User;
import com.qimu.qiapibackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: QiMu
 * @Date: 2023/08/21 10:06:40
 * @Version: 1.0
 * @Description: 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户电子邮件注册
     *
     * @param userEmailRegisterRequest 用户电子邮件注册请求
     * @return long
     */
    long userEmailRegister(UserEmailRegisterRequest userEmailRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户
     * 获取当前登录用户
     *
     * @param request 请求
     * @return {@link UserVO}
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 是管理
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是游客
     *
     * @param request 要求
     * @return {@link User}
     */
    User isTourist(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return boolean
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 校验
     *
     * @param add  是否为创建校验
     * @param user 接口信息
     */
    void validUser(User user, boolean add);

    /**
     * 更新凭证
     * 凭证
     *
     * @param loginUser 登录用户
     * @return {@link UserVO}
     */
    UserVO updateVoucher(User loginUser);

    /**
     * 添加钱包余额
     *
     * @param userId    用户id
     * @param addPoints 添加点
     * @return boolean
     */
    boolean addWalletBalance(Long userId, Integer addPoints);

    /**
     * 减少钱包余额
     *
     * @param userId      用户id
     * @param reduceScore 减少分数
     * @return boolean
     */
    boolean reduceWalletBalance(Long userId, Integer reduceScore);

    /**
     * 用户电子邮件登录
     *
     * @param userEmailLoginRequest 用户电子邮件登录请求
     * @param request               要求
     * @return {@link UserVO}
     */
    UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request);

    /**
     * 用户绑定电子邮件
     *
     * @param userEmailLoginRequest 用户电子邮件登录请求
     * @param request               要求
     * @return {@link UserVO}
     */
    UserVO userBindEmail(UserBindEmailRequest userEmailLoginRequest, HttpServletRequest request);

    /**
     * 用户取消绑定电子邮件
     *
     * @param request                要求
     * @param userUnBindEmailRequest 用户取消绑定电子邮件请求
     * @return {@link UserVO}
     */
    UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request);
}
