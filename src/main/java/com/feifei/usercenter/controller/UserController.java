package com.feifei.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feifei.usercenter.common.BaseResponse;
import com.feifei.usercenter.common.ErrorCode;
import com.feifei.usercenter.common.ResultUtils;
import com.feifei.usercenter.exception.BussinessException;
import com.feifei.usercenter.model.User;
import com.feifei.usercenter.model.request.UserLoginRequest;
import com.feifei.usercenter.model.request.UserRegisterRequest;
import com.feifei.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.feifei.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.feifei.usercenter.constant.UserConstant.User_Login_States;
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
     public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest==null){
           throw  new BussinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode=userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount,userAccount,checkPassword,planetCode)){
            throw  new BussinessException(ErrorCode.NULL_ERROR);
        }

        Long id = userService.register(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(id);
    }
    @PostMapping("/login")
    public BaseResponse<User> UserRegister(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
        if (userLoginRequest==null){
            throw  new BussinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,userAccount)){
            throw  new BussinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.doLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest httpServletRequest){
        Object userObj = httpServletRequest.getSession().getAttribute(User_Login_States);
        User currentUser=(User)userObj;
        if (currentUser==null){
            throw  new BussinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        //todo 校验用户是否合法
        Integer id = currentUser.getId();
        User user=userService.getSafetyUser(userService.getById(id));
        return  ResultUtils.success(user);
    }


    /**
     * 查询用户
     * 只有管理员才能查询用户
     * @param username
     * @return
     */
    @GetMapping ("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest httpServletRequest){
       if (!isAdmin(httpServletRequest)){
           throw  new BussinessException(ErrorCode.NO_AUTH);
       }
        QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)){
            userQueryWrapper.like("username",username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        List<User> newList=userList.stream().map(user ->
                userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(newList);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(int id,HttpServletRequest httpServletRequest){
       if (!isAdmin(httpServletRequest)){
           throw  new BussinessException(ErrorCode.NO_AUTH);
       }
        if (id<=0){
            throw  new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result=userService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest httpServletRequest){
        if (httpServletRequest==null){
            throw  new BussinessException(ErrorCode.NULL_ERROR);
        }
        int result=userService.userLogout(httpServletRequest);
       return ResultUtils.success(result);
    }

    /**
     * 鉴权，判断是否有管理员权限，只有管理员才可以对用户进行查询和删除
     * @param httpServletRequest
     * @return
     */
      private Boolean isAdmin(HttpServletRequest httpServletRequest){
          //鉴权
          Object userObj = httpServletRequest.getSession().getAttribute(User_Login_States);
          User user=(User)userObj;
          if (user==null||user.getUserRole()!=ADMIN_ROLE){
              return  false;
          }
          return  true;
      }

}
