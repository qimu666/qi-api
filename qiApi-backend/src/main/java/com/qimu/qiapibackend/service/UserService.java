package com.qimu.qiapibackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
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
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param userName      用户名
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userName);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户
     * 获取当前登录用户
     *
     * @param request 请求
     * @return {@link User}
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是管理
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

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
}
