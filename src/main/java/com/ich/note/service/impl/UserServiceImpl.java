package com.ich.note.service.impl;

import com.ich.note.dao.IUserDao;
import com.ich.note.dao.IUserLogDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.User;
import com.ich.note.service.IUserService;
import com.ich.note.util.EventCode;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import static com.ich.note.pojo.table.Tables.USER;

import java.util.Map;

/**
 * @className: UserServiceImpl
 * @Description: 用户业务的实现层
 * @Author: ich
 */
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao; // 用户的数据库接口
    @Autowired
    private IUserLogDao userLogDao; // 用户日志的数据库接口

    /**
     * 登录（邮箱号）
     * @param email 邮箱号
     * @param password 密码
     * @return { user：登录成功后的用户信息，userToken：存到 redis 中的用户信息查询关键词 }
     */
    @Override
    public Map loginByEmailAndPassword(String email, String password) throws ServiceException {
//        根据邮箱和密码查询用户记录
        QueryWrapper wrapper = QueryWrapper.create()
                .select(USER.ID, USER.EMAIL, USER.NICKNAME, USER.HEAD_PIC, USER.LEVEL, USER.STATUS, USER.TIME)
                .where(USER.EMAIL.eq(email))
                .and(USER.PASSWORD.eq(password));

        User user = null;
        try {
            user = userDao.selectOneByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
//            执行查询错误
            throw new ServiceException("登录服务错误", EventCode.SELECT_EXCEPTION);
        }

        if (user == null) throw new ServiceException("账号或密码错误", EventCode.SELECT_NONE);

        if (user.getStatus() == 0) throw new ServiceException("账号被锁定", EventCode.ACCOUNT_CLOCK);

        return null;
    }
}
