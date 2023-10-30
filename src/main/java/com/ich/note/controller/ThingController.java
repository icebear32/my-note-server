package com.ich.note.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Thing;
import com.ich.note.pojo.User;
import com.ich.note.service.IThingService;
import com.ich.note.util.EventCode;
import com.ich.note.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className: ThingController
 * @Description: 关于小记的控制层
 * @Author: ich
 */
@RestController
@RequestMapping("/thing") // 请求地址：http://127.0.0.1:18081/ich-notes/thing
public class ThingController {

    @Autowired
    private IThingService thingService; // 用户的业务
    @Autowired
    private StringRedisTemplate redisTemplate; // redis 对象

    /**
     * 获取用户的小记列表
     * 请求地址：http://127.0.0.1:18081/ich-notes/thing/list
     * 请求方式：GET
     *
     * @param userToken redis key，登录用户的信息
     * @return 响应数据
     */
    @GetMapping("/list")
    public ResponseData getUserThingList(@RequestHeader String userToken) {
//        判断userToken 是否为空
        if (Validator.isEmpty(userToken)) return new ResponseData(false, "登录状态有误", EventCode.PARAM_USER_TOKEN_WRONG);

        String userTokenRedisValue = null;
        try {
//            从 redis 中获取登录用户的信息
            userTokenRedisValue = redisTemplate.opsForValue().get(userToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(false, "小记服务错误", EventCode.REDIS_SERVE_ERROR);
        }

//        判断是否登录失效
        if (Validator.isEmpty(userTokenRedisValue)) return new ResponseData(false, "登录失效", EventCode.LOGIN_INVALID);

//        登录的用户对象
        User user = JSONUtil.toBean(userTokenRedisValue, User.class);

        try {
//            调用用户的小记列表业务
            List<Thing> things = thingService.getUserNormalThing(user.getId());
            return new ResponseData(true, "获取成功", EventCode.SELECT_SUCCESS, things);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }
}
