package com.feifei.usercenter.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.util.DigestUtils;

@SpringBootTest
public class UserServiceTest {


    @Autowired
    private UserService userService;

    @Test
    public void Digest(){
        String SALT="feifei";
        String newPassword= DigestUtils.md5DigestAsHex((SALT+"abdc").getBytes());
        System.out.println(newPassword);
    }

    @Test
    void register() {
        String userAccount="feif";
        String userPassword="";
        String checkPassword="123456";
        String planetCode="123";
        long result=userService.register(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="fe";
        userPassword="123456";
        result = userService.register(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="12l*";
        result = userService.register(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="feif";
        userPassword="1234567";
        result = userService.register(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="1234";
        result = userService.register(userAccount,userPassword,checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount="feifei";
        userPassword="123456";
        result = userService.register(userAccount,userPassword,checkPassword,planetCode);
    }
}
