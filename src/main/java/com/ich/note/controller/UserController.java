package com.ich.note.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import com.ich.note.exception.ServiceException;
import com.ich.note.service.IUserService;
import com.ich.note.util.EventCode;
import com.ich.note.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @className: UserController
 * @Description: 关于用户的控制层
 * @Author: ich
 */
@RestController
@RequestMapping("/user") // 请求地址：http://127.0.0.1:18081/user
public class UserController {

    @Autowired
    private IUserService userService; // 用户的业务
    @Autowired
    private StringRedisTemplate redisTemplate; // redis 对象

    /**
     * 退出登录
     * 请求地址：http://127.0.0.1:18081/user/login/out
     * 请求方式：GET
     *
     * @param userToken 删除 redis 中的关键词
     * @return 响应数据
     */
    @GetMapping("/login/out")
    public ResponseData signOutLogin(@RequestHeader String userToken) {
//        判断userToken 是否为空
        if (Validator.isEmpty(userToken)) return new ResponseData(false, "登录状态有误", EventCode.PARAM_USER_TOKEN_WRONG);

        try {
            redisTemplate.delete(userToken);
            return new ResponseData(true, "退出登录成功", EventCode.LOGIN_OUT_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(false, "退出登录失败", EventCode.LOGIN_OUT_EXCEPTION);
        }
    }

    /**
     * 注册（邮箱）
     * 请求地址：http://127.0.0.1:18081/user/register/email
     * 请求方式：POST
     *
     * @param email 邮箱号
     * @param vc 验证码
     * @param vcKey 验证码查询的关键词
     * @return 响应数据 { user, userToken }
     */
    @PostMapping("/register/email")
    public ResponseData registerAccountByEmail(String email, String vc, String vcKey) {
//        验证邮箱参数是否为空
        if (Validator.isEmpty(email)) return new ResponseData(false, "邮箱参数有误", EventCode.ACCOUNT_EMAIL_WRONG);
//        验证验证码参数是否为空
        if (Validator.isEmpty(vc)) return new ResponseData(false, "验证码参数有误", EventCode.PARAM_VC_WRONG);
//        验证码查询的关键词是否为空
        if (Validator.isEmpty(vcKey)) return new ResponseData(false, "验证码查询的关键词参数有误", EventCode.PARAM_VC_KEY_WRONG);
//        判断注册的邮箱号是否为发送的验证码的邮箱
        String vc_email = vcKey.split(":")[1];
        if (!CharSequenceUtil.equals(email, vc_email)) {
            return new ResponseData(false, "注册邮箱已发生改变，请重新获取验证码", EventCode.PARAM_VC_KEY_EMAIL_WRONG);
        }

//        从 redis 中获取真实的验证码
        String vcTokenValue = redisTemplate.opsForValue().get(vcKey);
//        判断验证码是否已失效
        if (Validator.isEmpty(vcTokenValue)) return new ResponseData( false, "验证码已失效，请重新获取验证码", EventCode.VC_INVALID);
//        判断验证码是否准确
        if(!CharSequenceUtil.equals(vcTokenValue, vc)) {
            return new ResponseData(false,"验证码错误", EventCode.VC_MATCH_ERROR);
        }

        try {
//            调用邮箱注册账号业务
            userService.registerAccountByEmail(email);
            return new ResponseData(true, "注册成功", EventCode.ACCOUNT_EMAIL_REGISTER_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

    /**
     * 登录（邮箱和密码）
     * 请求地址：http://127.0.0.1:18081/user
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
