package com.ich.note.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
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
     * 根据邮箱注册账号
     * @param email 邮箱号
     * @throws ServiceException 业务异常
     */
    @Override
    public void registerAccountByEmail(String email) throws ServiceException {
//        获取邮箱是否已被注册
        getEmailAccountIsExist(email);

//        新增用户的记录
        Date localTime = new Date(); // 时间
        String password = RandomUtil.randomString(6); // 初始密码

        User user = User.builder()
                .email(email)
                .password(SecureUtil.md5(password)) // 密码随机生成，并且加密
                .time(localTime)
                .build();

        int count = 0;
        try {
            count = userDao.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("注册失败", EventCode.INSERT_EXCEPTION);
        }

        if (count != 1) throw new ServiceRollbackException("注册失败", EventCode.INSERT_ERROR);

//        新增一个用户日志（注册）
        UserLog log = UserLog.builder()
                .event(EventCode.ACCOUNT_EMAIL_REGISTER_SUCCESS)
                .desc("邮箱注册账号")
                .time(localTime)
                .userId(user.getId())
                .build();

        try {
            count = userLogDao.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceRollbackException("注册失败", EventCode.ACCOUNT_EMAIL_REGISTER_LOG_EXCEPTION);
        }

        if (count != 1) throw new ServiceRollbackException("注册失败", EventCode.ACCOUNT_EMAIL_REGISTER_LOG_ERROR);

//        邮箱通知注册的用户，其初始密码
        String content = "<p>【ich团队】尊敬的" + email + "：</p>" +
                "<p>您已成功注册ich笔记账号，其初始密码为：" +  "<b style='font-size: 20px;color: blue;'>" + password + "</b>。</p>" +
                "<p>请尽量快速登录账号，修改其初始密码！</p>";

        try {
            MailUtil.send(email,"ich账号注册通知", content,true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceRollbackException("注册失败", EventCode.EMAIL_SEND_INIT_PASSWORD_EXCEPTION);
        }
    }

    /**
     * 获取邮箱是否被注册
     * @param email 邮箱号
     * @throws ServiceException 业务异常
     */
    @Override
    public void getEmailAccountIsExist(String email) throws ServiceException {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(USER.EMAIL.eq(email));

        long count = 0;
        try {
            count = userDao.selectCountByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("查询服务异常", EventCode.SELECT_EXCEPTION);
        }

        if (count != 0) throw new ServiceException("该邮箱账号已被注册", EventCode.ACCOUNT_EMAIL_REGISTERED);
    }

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
        UserLog log = UserLog.builder()
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
