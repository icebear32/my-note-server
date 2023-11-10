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

import java.util.Date;
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
     * 获取编辑的小记信息
     * 请求地址：http://127.0.0.1:18081/ich-notes/thing/edit
     * 请求方式：GET
     *
     * @param thingId 小记编号
     * @param userToken redis key，登录用户的信息
     * @return 响应数据，小记对象
     */
    @GetMapping("/edit")
    public ResponseData getUserEditThing(int thingId, @RequestHeader String userToken) {
        try {
//            判断登录参数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            验证小记编号参数
            if (Validator.isEmpty(thingId)) return new ResponseData(false, "小记编号参数有误", EventCode.PARAM_THING_ID_WRONG);
//            调用获取编辑小记业务
            Thing editThing = thingService.getEditThing(thingId, user.getId());
            return new ResponseData(true, "获取成功", EventCode.SELECT_SUCCESS, editThing);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

    /**
     * 新增小记
     * 请求地址：http://127.0.0.1:18081/ich-notes/thing/create
     * 请求方式：POST
     *
     * @param title 标题
     * @param top 是否置顶
     * @param tags 标签（"六一,礼物,儿童节"）
     * @param content 待办事项（[{"checked":true,"thing":"气球"},{"checked":true,"thing":"棒棒糖"}]）
     * @param finished 是否完成
     * @param userToken redis key，登录用户的信息
     * @return
     */
    @PostMapping("/create")
    public ResponseData createThing(String title, boolean top, String tags, String content, boolean finished, @RequestHeader String userToken) {
        try {
//            判断登陆参数
            User user = TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            验证标题参数
            if (Validator.isEmpty(title))
                return new ResponseData(false, "小记标题参数有误", EventCode.PARAM_THING_TITLE_WRONG);
//            验证置顶参数
            if (Validator.isEmpty(top))
                return new ResponseData(false, "小记置顶参数有误", EventCode.PARAM_THING_TOP_WRONG);
//            验证标签参数
            if (Validator.isEmpty(tags))
                return new ResponseData(false, "小记标签参数有误", EventCode.PARAM_THING_TAGS_WRONG);
//            验证内容参数
            if (Validator.isEmpty(content))
                return new ResponseData(false, "小记内容参数有误", EventCode.PARAM_THING_CONTENT_WRONG);
//            验证完成参数
            if (Validator.isEmpty(finished))
                return new ResponseData(false, "小记完成参数有误", EventCode.PARAM_THING_FINISHED_WRONG );

//            时间
            Date localTime = new Date();

            Thing thing = Thing.builder()
                    .updateTime(localTime)
                    .time(localTime)
                    .title(title)
                    .tags(tags)
                    .content(content)
                    .userId(user.getId())
                    .finished(finished ? 1 : 0)
                    .top(top ? 1 : 0)
                    .build();

//            调用新增小记业务
            thingService.newCreateThing(thing);
            return new ResponseData(true, "新增小记成功", EventCode.THING_CREATE_SUCCESS);
        } catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

    /**
     * 删除小记（彻底删除小记）
     * 请求地址：http://127.0.0.1:18081/ich-notes/thing/delete
     * 请求方式：DELETE
     *
     * @param complete 是否为彻底删除
     * @param thingId 小记编号
     * @param isRecycleBin 是否为回收站操作
     * @param userToken redis key，登录用户的信息
     * @return 响应数据
     */
    @DeleteMapping("/delete")
    public ResponseData deleteThing(boolean complete, int thingId, boolean isRecycleBin, @RequestHeader String userToken) {
        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            验证彻底删除参数
            if (Validator.isEmpty(complete)) return new ResponseData(false, "删除参数有误", EventCode.PARAM_DELETE_COMPLETE_WRONG);
//            验证回收站参数
            if (Validator.isEmpty(isRecycleBin)) return new ResponseData(false, "删除参数有误", EventCode.PARAM_DELETE_RECYCLE_WRONG);
//            验证小记编号参数
            if (Validator.isEmpty(thingId)) return new ResponseData(false, "小记编号参数有误", EventCode.PARAM_THING_ID_WRONG);
//            调用删除小记业务
            thingService.deleteThingById(complete, thingId, user.getId(), isRecycleBin);
            return new ResponseData(true, complete ? "彻底删除成功" : "删除成功", EventCode.UPDATE_SUCCESS);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

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
