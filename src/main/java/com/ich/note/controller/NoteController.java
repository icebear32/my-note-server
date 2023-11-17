package com.ich.note.controller;

import cn.hutool.core.lang.Validator;
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
import org.springframework.web.bind.annotation.*;

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
     * 新增笔记
     * 请求地址：http://127.0.0.1:18081/note/create
     * 请求方式：PUT
     *
     * @param userToken redis key，登录用户的信息
     * @return 响应数据
     */
    @DeleteMapping("/create")
    public ResponseData createNote(@RequestHeader String userToken) {
        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            调用删除笔记业务
            int noteId = noteService.createNoteInit(user.getId());
            return new ResponseData(true,"创建成功", EventCode.NT_CREATE_SUCCESS, noteId);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

    /**
     * 删除笔记（彻底删除笔记）
     * 请求地址：http://127.0.0.1:18081/note/delete
     * 请求方式：DELETE
     *
     * @param complete 是否为彻底删除
     * @param noteId 笔记编号
     * @param isRecycleBin 是否为回收站操作
     * @param userToken redis key，登录用户的信息
     * @return 响应数据
     */
    @DeleteMapping("/delete")
    public ResponseData deleteNote(boolean complete, int noteId, boolean isRecycleBin, @RequestHeader String userToken) {
        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            验证彻底删除参数
            if (Validator.isEmpty(complete)) return new ResponseData(false, "删除参数有误", EventCode.PARAM_DELETE_COMPLETE_WRONG);
//            验证回收站参数
            if (Validator.isEmpty(isRecycleBin)) return new ResponseData(false, "删除参数有误", EventCode.PARAM_DELETE_RECYCLE_WRONG);
//            验证笔记编号参数
            if (Validator.isEmpty(noteId)) return new ResponseData(false, "笔记编号参数有误", EventCode.PARAM_ID_WRONG);
//            调用删除笔记业务
            noteService.deleteNoteById(complete, noteId, user.getId(), isRecycleBin);
            return new ResponseData(true, complete ? "彻底删除成功" : "删除成功", EventCode.UPDATE_SUCCESS);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

    /**
     * 置顶笔记（取消置顶笔记）
     * 请求地址：http://127.0.0.1:18081/note/top
     * 请求方式：GET
     *
     * @param isTop 是否为置顶
     * @param noteId 笔记编号
     * @param userToken redis key，登录用户的信息
     * @return
     */
    @GetMapping("top")
    public ResponseData topNote(boolean isTop, int noteId, @RequestHeader String userToken) {

        try {
//            判断登录参数
            User user =  TokenValidateUtil.validateUserToken(userToken, redisTemplate);
//            验证登录参数
            if (Validator.isEmpty(isTop)) return new ResponseData(false, "置顶参数有误", EventCode.PARAM_TOP_WRONG);
//            验证笔记编号参数
            if (Validator.isEmpty(noteId)) return new ResponseData(false, "笔记编号参数有误", EventCode.PARAM_ID_WRONG);
//            调用置顶笔记业务
            noteService.topNote(isTop, noteId, user.getId());
            return new ResponseData(true, isTop ? "置顶成功" : "取消置顶成功", EventCode.UPDATE_SUCCESS);
        }catch (ServiceException e) {
            e.printStackTrace();
            return new ResponseData(false, e.getMessage(), e.getCode());
        }
    }

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
