package com.ich.note.service;

import com.ich.note.exception.ServiceException;

import java.util.Map;

// 业务接口：邮箱的业务
public interface IMailService {

    /**
     * 获取邮箱注册的验证码
     * @param email 邮箱号
     * @return 查询验证码的关键词（redis）
     * @throws ServiceException 业务异常
     */
    String getEmailRegisterVC(String email) throws ServiceException;
}
