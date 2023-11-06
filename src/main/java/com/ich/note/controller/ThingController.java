package com.ich.note.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ValidateParmException;
import com.ich.note.pojo.Thing;
import com.ich.note.pojo.User;
import com.ich.note.service.IThingService;
import com.ich.note.util.EventCode;
import com.ich.note.util.response.ResponseData;
import com.ich.note.util.validate.TokenValidateUtil;
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
     * 置顶小记（取消置顶小记）
     * 请求地址：http://127.0.0.1:18081/ich-notes/thing/top
     * 请求方式：GET
     *
     * @param isTop 是否为置顶
     * @param thingId 小记编号
     * @param userToken redis key，登录用户的信息
     * @return
     */
    @GetMapping("top")
    public ResponseData topThing(boolean isTop, int thingId, @RequestHeader String userToken) {

        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            验证登录参数
            if (Validator.isEmpty(isTop)) return new ResponseData(false, "置顶参数有误", EventCode.PARAM_TOP_WRONG);
//            验证小记编号参数
            if (Validator.isEmpty(thingId)) return new ResponseData(false, "小记编号参数有误", EventCode.PARAM_THING_ID_WRONG);
//            调用置顶小记业务
            thingService.topThing(isTop, thingId, user.getId());
            return new ResponseData(true, isTop ? "置顶成功" : "取消置顶成功", EventCode.UPDATE_SUCCESS);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

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

        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            调用用户的小记列表业务
            List<Thing> things = thingService.getUserNormalThing(user.getId());
            return new ResponseData(true, "获取成功", EventCode.SELECT_SUCCESS, things);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }
}
