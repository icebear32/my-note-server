package com.ich.note.service;

import com.ich.note.exception.ServiceException;

import java.util.Map;

// 业务接口：用户的业务
public interface IUserService {

    /**
     * 根据邮箱注册账号
     * @param email 邮箱号
     * @throws ServiceException 业务异常
     */
    void registerAccountByEmail(String email) throws ServiceException;

    /**
     * 获取邮箱是否被注册
     * @param email 邮箱号
     * @throws ServiceException 业务异常
     */
    void getEmailAccountIsExist(String email) throws ServiceException;

    /**
     * 登录（邮箱号）
     * @param email 邮箱号
     * @param password 密码
     * @return { user：登录成功后的用户信息，userToken：存到 redis 中的用户信息查询关键词 }
     */
    Map loginByEmailAndPassword(String email, String password) throws ServiceException;
}
