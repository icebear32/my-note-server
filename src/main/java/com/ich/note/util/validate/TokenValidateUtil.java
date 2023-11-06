package com.ich.note.util.validate;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import com.ich.note.exception.ValidateParmException;
import com.ich.note.pojo.User;
import com.ich.note.util.EventCode;
import org.springframework.data.redis.core.StringRedisTemplate;

public class TokenValidateUtil {
    public static User validateUserToken(String userToken, StringRedisTemplate redisTemplate) throws ValidateParmException {
//        判断userToken 是否为空
        if (Validator.isEmpty(userToken))
            throw new ValidateParmException("登录状态有误", EventCode.PARAM_USER_TOKEN_WRONG);

        String userTokenRedisValue = null;
        try {
//            从 redis 中获取登录用户的信息
            userTokenRedisValue = redisTemplate.opsForValue().get(userToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidateParmException("服务错误", EventCode.REDIS_SERVE_ERROR);
        }

//        判断是否登录失效
        if (Validator.isEmpty(userTokenRedisValue))
            throw new ValidateParmException("登录失效", EventCode.LOGIN_INVALID);

//        登录的用户对象
        return JSONUtil.toBean(userTokenRedisValue, User.class);
    }
}
