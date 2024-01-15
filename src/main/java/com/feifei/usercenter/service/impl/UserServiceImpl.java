package com.feifei.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feifei.usercenter.common.ErrorCode;
import com.feifei.usercenter.exception.BussinessException;
import com.feifei.usercenter.model.User;
import com.feifei.usercenter.service.UserService;
import com.feifei.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.feifei.usercenter.constant.UserConstant.User_Login_States;

/**
* @author 23176
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-11-17 20:42:52
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    public static final String SALT="feifei";

    @Override
    public long register(String userAccount, String userPassword, String checkPassword,String planetCode) {
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
           throw  new BussinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length()<4){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR,"账号长度不能过短");
        }
        if (userPassword.length()<6||checkPassword.length()<6){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR,"密码长度不能过短");
        }

        String validPattern ="[`~!@#$%^&*()=<>?:,.:;'\\\\[\\\\]·~！/@#￥%……&*（）——\\-+={}|《》？：“”【】、；‘'，。、?]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"账号不能包括特殊字符");
        }

        //密码和校验码相同
        if(!userPassword.equals((checkPassword))){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        //校验星球标号
        if(planetCode.length()>5){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR,"星球编号太长");
        }

        //校验是否有重复的账号
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long  count=userMapper.selectCount(queryWrapper);
        if (count>0){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"该账号已被注册");
        }


        //校验是否有重复的星球编号
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count=userMapper.selectCount(queryWrapper);
        if (count>0){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR,"该星球编号已有用户使用");
        }
        //加密

        String newPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        User user=new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        user.setPlanetCode(planetCode);
        Boolean result=this.save(user);
        if (!result){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"注册用户失败");
        }
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        //1.校验用户账户和密码是否合法
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length()<4){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userPassword.length()<6){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR);
        }

        String validPattern ="[`~!@#$%^&*()=<>?:,.:;'\\\\[\\\\]·~！/@#￥%……&*（）——\\-+={}|《》？：“”【】、；‘'，。、?]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.加密
        String newPassword= DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",newPassword);
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
        if (user==null){
            log.info("login failed,userAccount not match userPassword");
            throw  new BussinessException(ErrorCode.NULL_ERROR);
         }
        User safetyUser = getSafetyUser(user);
        //4.记录用户登录态
        httpServletRequest.getSession().setAttribute(User_Login_States,safetyUser);
        return safetyUser;
    }

    @Override
    public  User getSafetyUser(User user){
        //3.用户脱敏
        if(user==null){
            throw  new BussinessException(ErrorCode.NULL_ERROR);
        }
        User safetyUser=new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest httpServletRequest) {
        //移除登录态
       httpServletRequest.getSession().removeAttribute(User_Login_States);
       return 1;
    }
}




