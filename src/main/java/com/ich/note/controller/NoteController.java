package com.ich.note.controller;

import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Note;
import com.ich.note.pojo.Thing;
import com.ich.note.pojo.User;
import com.ich.note.service.INoteService;
import com.ich.note.util.EventCode;
import com.ich.note.util.response.ResponseData;
import com.ich.note.util.validate.TokenValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @className: NoteController
 * @Description: 关于笔记的控制层
 * @Author: ich
 */
@RestController
@RequestMapping("/note") // 请求地址：http://127.0.0.1:18081/note
public class NoteController {

    @Autowired
    private INoteService noteService; // 笔记的业务接口
    @Autowired
    private StringRedisTemplate redisTemplate; // redis 对象

    /**
     * 获取用户的小记列表
     * 请求地址：http://127.0.0.1:18081/note/list
     * 请求方式：GET
     *
     * @param userToken redis key，登录用户的信息
     * @return 响应数据
     */
    @GetMapping("/list")
    public ResponseData getUserNoteList(@RequestHeader String userToken) {

        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            调用用户的笔记列表业务
            List<Note> notes = noteService.getUserNormalNotes(user.getId());
            return new ResponseData(true, "获取成功", EventCode.SELECT_SUCCESS, notes);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }
}
