package com.ich.note.controller;

import cn.hutool.core.lang.Validator;
import com.ich.note.exception.ServiceException;
import com.ich.note.service.IMailService;
import com.ich.note.util.EventCode;
import com.ich.note.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail") // 请求地址：http://127.0.0.1:18081/mail
public class MailController {

    @Autowired
    private IMailService mailService; // 邮箱的业务

    /**
     * 获取邮箱注册账号的验证码
     * 请求地址：http://127.0.0.1:18081/user
     * 请求方式：GET
     *
     * @param email 邮箱号
     * @return 响应数据：查询验证码的关键词
     */
    @GetMapping("/register/vc")
    public ResponseData getEmailRegisterAccountVC(String email) {
//        验证邮箱参数是否为空
        if (Validator.isEmpty(email)) return new ResponseData(false, "邮箱参数有误", EventCode.ACCOUNT_EMAIL_WRONG);

        try {
//          调用邮箱注册账号的验证码业务
            String registerVC = mailService.getEmailRegisterVC(email);
            return new ResponseData(true, "发送成功", EventCode.EMAIL_SEND_VC_SUCCESS, registerVC);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }
}
