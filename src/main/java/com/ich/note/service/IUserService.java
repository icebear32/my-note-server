package com.ich.note.service;

import com.ich.note.exception.ServiceException;

import java.util.Map;

public interface IUserService {

    /**
     * 登录（邮箱号）
     * @param email 邮箱号
     * @param password 密码
     * @return { user：登录成功后的用户信息，userToken：存到 redis 中的用户信息查询关键词 }
     */
    Map loginByEmailAndPassword(String email, String password) throws ServiceException;
}
