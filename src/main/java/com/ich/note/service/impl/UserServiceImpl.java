package com.ich.note.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.ich.note.dao.IUserDao;
import com.ich.note.dao.IUserLogDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ServiceRollbackException;
import com.ich.note.pojo.User;
import com.ich.note.pojo.UserLog;
import com.ich.note.service.IUserService;
import com.ich.note.util.EventCode;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ich.note.pojo.table.Tables.USER;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @className: UserServiceImpl
 * @Description: 用户业务的实现层
 * @Author: ich
 */
@Service
@Transactional(rollbackFor = {ServiceRollbackException.class})
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao; // 用户的数据库接口
    @Autowired
    private IUserLogDao userLogDao; // 用户日志的数据库接口
    @Autowired
    private StringRedisTemplate redisTemplate; // redis 对象

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

//        新增用户日志（登录）
        UserLog log =UserLog.builder()
                .event(EventCode.LOGIN_EMAIL_PASSWORD_SUCCESS)
                .desc("邮箱密码登录")
                .time(new Date())
                .userId(user.getId())
                .build();

        int count = 0;
        try {
            count = userLogDao.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("登录服务错误", EventCode.LOGIN_LOG_CREATE_EXCEPTION);
        }

        if (count != 1) throw new ServiceRollbackException("登录服务错误", EventCode.LOGIN_LOG_CREATE_FAIL);

//        将登录的信息存储在 redis 中，14天，并将查询登录用户的关键词返回给客户端
//        生成唯一的 key 值
        String userTokenKey = "userToken:" + IdUtil.randomUUID();

        try {
            redisTemplate.opsForValue().set(
                    userTokenKey,
                    JSONUtil.toJsonStr(user),
                    14,
                    TimeUnit.DAYS
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceRollbackException("登录服务错误", EventCode.LOGIN_SAVE_USER_TOKEN_REDIS_EXCEPTION);
        }

//        将登录的用户信息和查询 redis 用户信息的关键词返回出去
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("userToken", userTokenKey);

        return map;
    }
}
