package com.feifei.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 2459747691338849919L;
    private String userAccount;
    private String userPassword;
}
