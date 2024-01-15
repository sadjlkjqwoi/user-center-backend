package com.feifei.usercenter.service;

import com.feifei.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 23176
* @description 针对表【user】的数据库操作Service
* @createDate 2023-11-17 20:42:52
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @param planetCode
     * @return
     */
    long register(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param httpServletRequest
     * @return
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 用户注销
     * @param httpServletRequest
     */
    int userLogout(HttpServletRequest httpServletRequest);

}
