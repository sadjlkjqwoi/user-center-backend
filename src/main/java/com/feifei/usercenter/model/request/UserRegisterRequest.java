package com.feifei.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3943682681324956361L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
