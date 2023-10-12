package com.ich.note.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.ich.note.exception.ServiceException;
import com.ich.note.service.IUserService;
import com.ich.note.util.EventCode;
import com.ich.note.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @className: UserController
 * @Description: 关于用户的控制层
 * @Author: ich
 */
@RestController
@RequestMapping("/user") // 请求地址：http://127.0.0.1:18081/ich-notes/user
public class UserController {

    @Autowired
    private IUserService userService; // 用户的业务

    /**
     * 登录（邮箱和密码）
     * 请求地址：http://127.0.0.1:18081/ich-notes/user
     * 请求方式：POST
     *
     * @param email 邮箱号
     * @param password 密码
     * @return 响应数据 { user, userToken }
     */
    @PostMapping("/login/email/password")
    public ResponseData loginByEmailAndPassword(String email, String password) {
//        验证邮箱参数是否为空
        if (Validator.isEmpty(email)) return new ResponseData(false, "邮箱参数有误", EventCode.ACCOUNT_EMAIL_WRONG);
//        验证密码参数是否为空
        if (Validator.isEmpty(password)) return new ResponseData(false, "密码参数有误", EventCode.ACCOUNT_EMAIL_PASSWORD);

//        密码加密
        password = SecureUtil.md5(password);

        try {
//            调用邮箱密码登录业务
            Map loginMap = userService.loginByEmailAndPassword(email, password);
            return new ResponseData(true, "登陆成功", EventCode.LOGIN_EMAIL_PASSWORD_SUCCESS, loginMap);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }
}
