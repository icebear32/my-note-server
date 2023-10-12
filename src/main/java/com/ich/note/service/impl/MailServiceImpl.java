package com.ich.note.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailUtil;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ServiceRollbackException;
import com.ich.note.service.IMailService;
import com.ich.note.service.IUserService;
import com.ich.note.util.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @className: MailServiceImpl
 * @Description: 邮箱业务的实现层
 * @Author: ich
 */
@Service
@Transactional(rollbackFor = {ServiceRollbackException.class})
public class MailServiceImpl implements IMailService {

    @Autowired
    private IUserService userService; // 用户的业务
    @Autowired
    private StringRedisTemplate redisTemplate; // redis 对象

    /**
     * 获取邮箱注册的验证码
     * @param email 邮箱号
     * @return 查询验证码的关键词（redis）
     * @throws ServiceException
     */
    @Override
    public String getEmailRegisterVC(String email) throws ServiceException {

//        先判断该邮箱是否已被注册过
        userService.getEmailAccountIsExist(email);

//        发送验证码到注册的邮箱中
        String code = RandomUtil.randomString(6); // 随机生成 6 位字符（数值和字母）

        int time = 15;

        String content = "<p>【ich团队】尊敬的" + email + "：</p>" +
                "<p>您正在申请注册账号服务，如本人操作，请勿泄露该验证码！</p>" +
                "<p>验证码为：<b style='font-size: 20px;color: blue;'>" + code + "</b></p>" +
                "有效时间为 " + time + " 分钟";

        try {
            MailUtil.send(email, "邮箱账号注册验证码", content, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("验证码发送失败", EventCode.EMAIL_SEND_VC_ERROR);
        }

//        将验证码保存到 redis 中 15 分钟
        String eravcTokenKey = "eravcToken:" + email + ":" + IdUtil.randomUUID();

        try {
            redisTemplate.opsForValue().set(eravcTokenKey, code, time, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("验证码存储失败", EventCode.EMAIL_SEND_VC_SAVE_REDIS_ERROR);
        }

        return eravcTokenKey;
    }
}
