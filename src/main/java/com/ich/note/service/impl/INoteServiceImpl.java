package com.ich.note.service.impl;

import com.ich.note.dao.INoteDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ServiceRollbackException;
import com.ich.note.pojo.Note;
import com.ich.note.pojo.NoteThingLog;
import com.ich.note.service.INoteService;
import com.ich.note.service.INoteThingLogService;
import com.ich.note.util.EventCode;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ich.note.pojo.table.Tables.NOTE;

import java.util.Date;
import java.util.List;

/**
 * @className: INoteServiceImpl
 * @Description: 笔记业务的实现层
 * @Author: ich
 */
@Service
public class INoteServiceImpl implements INoteService {

    @Autowired
    private INoteDao noteDao; // 笔记的数据接口
    @Autowired
    private INoteThingLogService noteThingLogService; // 笔记小记的业务接口

    /**
     * 获取编辑的笔记信息
     *
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @return 小记对象
     * @throws ServiceException 业务异常
     */
    @Override
    public Note getEditNote(int noteId, int userId) throws ServiceException {

        QueryWrapper wrapper = QueryWrapper.create()
                .select(NOTE.UPDATE_TIME, NOTE.TITLE, NOTE.CONTENT)
                .where(NOTE.STATUS.eq(1))
                .and(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId));

        Note note = null;
        try {
            note = noteDao.selectOneByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("笔记获取失败，请稍后再试", EventCode.SELECT_EXCEPTION);
        }

        if (note == null) throw new ServiceException("笔记不存在，请稍后再试", EventCode.SELECT_NONE);

//        将笔记返回出去
        return note;
    }

    /**
     * 创建笔记（并且初始化笔记）
     * @param userId
     * @return userId 用户编号
     * @throws ServiceException 业务异常
     */
    @Override
    public int createNoteInit(int userId) throws ServiceException {

        Date localTime = new Date(); // 时间

//        新增笔记的对象
        Note note = Note.builder()
                .time(localTime)
                .updateTime(localTime)
                .userId(userId)
                .build();

        int count = 0;
        try {
            count = noteDao.insert(note);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("新建笔记失败", EventCode.INSERT_EXCEPTION);
        }

        if (count != 1) throw new ServiceRollbackException("新建笔记失败", EventCode.INSERT_ERROR);

//        新增笔记的日志对象
        NoteThingLog log = NoteThingLog.builder()
                .noteId(note.getId())
                .time(localTime)
                .event(EventCode.NT_CREATE_SUCCESS)
                .desc("新增笔记")
                .userId(userId)
                .build();

//        新增笔记日志
        noteThingLogService.addOneLog(log, true);

//        返回新增笔记的编号
        return note.getId();
    }

    /**
     * 根据编号删除笔记（彻底删除）
     *
     * @param complete 是否彻底删除
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站的操作
     * @throws ServiceException 业务异常
     */
    @Override
    public void deleteNoteById(boolean complete, int noteId, int userId, boolean isRecycleBin) throws ServiceException {
//        默认为正常删除操作，并不是彻底删除，也不是回收站中的删除
        String desc = "删除笔记";
        String event = EventCode.NT_DELETE_SUCCESS;
        int beforeStatus = 1; // 之前的状态
        int afterStatus = 0; // 删除之后的状态
        if (complete) {
            desc = "彻底删除笔记";
            event = EventCode.NT_COMPLETE_DELETE_SUCCESS;
            afterStatus = -1;
            if (isRecycleBin) beforeStatus = 0; // 在回收站中的笔记状态都是已删除的
        }

//        封装修改条件：WHERE `id` = ? AND `u_id` = ? AND `status` = ?(1/0)
        QueryWrapper wrapper = QueryWrapper.create()
                .where(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId))
                .and(NOTE.STATUS.eq(beforeStatus));

//        要操作的时间
        Date localTime = new Date();

//        要修改的哪些字段：status
        Note note = Note.builder()
                .status(afterStatus)
                .updateTime(localTime)
                .build();

        int count = 0;
        try {
//            调用修改语句（数据库接口）
            count = noteDao.updateByQuery(note, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(desc + "失败", EventCode.UPDATE_EXCEPTION);
        }

        if (count != 1) throw new ServiceRollbackException(desc + "失败", EventCode.UPDATE_ERROR);

//        添加小记的日志（删除）
        NoteThingLog log = NoteThingLog.builder()
                .time(localTime)
                .event(event)
                .desc(desc)
                .noteId(noteId)
                .userId(userId)
                .build();

//        新增笔记小记日志记录（删除业务）
        noteThingLogService.addOneLog(log, true);
    }

    /**
     * 置顶笔记（取消置顶笔记）
     * @param isTop 是否置顶笔记
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @throws ServiceException 业务异常
     */
    @Override
    public void topNote(boolean isTop, int noteId, int userId) throws ServiceException {
        String desc = "置顶笔记"; // 事件描述
        String eventSuccess = EventCode.NT_TOP_SUCCESS; // 事件代码
        int beforeTop = 0; // 修改之前的 top 字段值
        int afterTop = 1; // 修改之后的 top 字段值
        if (!isTop) {
            desc = "取消置顶笔记";
            eventSuccess = EventCode.NT_CANCEL_TOP_SUCCESS;
            beforeTop = 1;
            afterTop = 0;
        }

//        封装修改的条件：WHERE `id` = ? AND `u_id` = ? AND `status` = 1 AND `top` = 1/0
        QueryWrapper wrapper = QueryWrapper.create()
                .where(NOTE.ID.eq(noteId))
                .and(NOTE.USER_ID.eq(userId))
                .and(NOTE.STATUS.eq(1))
                .and(NOTE.TOP.eq(beforeTop));

//        封装修改的字段 top
        Note note = Note.builder().top(afterTop).build();

//        根据 wrapper 条件来修改小记的 top 字段
        int count = 0;
        try {
            count = noteDao.updateByQuery(note, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(desc + "服务异常", EventCode.UPDATE_EXCEPTION);
        }

        if (count != 1) {
            throw new ServiceRollbackException(desc + "服务异常", EventCode.UPDATE_ERROR);
        }

        Date localTime = new Date(); // 时间

//        新增笔记日志记录（置顶业务）
        NoteThingLog log = NoteThingLog.builder()
                .time(localTime)
                .event(eventSuccess)
                .desc(desc)
                .noteId(noteId)
                .userId(userId)
                .build();

//        新增笔记小记日志记录（置顶业务）
        noteThingLogService.addOneLog(log, true);
    }

    /**
     * 获取用户正常的笔记
     * @param userId 用户编号
     * @return 笔记列表
     * @throws ServiceException 业务异常
     */
    @Override
    public List<Note> getUserNormalNotes(int userId) throws ServiceException {

//        查询字段：编号，标题，内容，是否置顶，最后操作时间
//        查询条件：`status` = 1 AND `u_id` = ?
//        排序规则：置顶靠前，最后操作的时间离当前时间最近靠前
        QueryWrapper wrapper = QueryWrapper.create()
                .select(NOTE.ID, NOTE.TITLE, NOTE.BODY, NOTE.TOP, NOTE.UPDATE_TIME)
                .where(NOTE.STATUS.eq(1))
                .and(NOTE.USER_ID.eq(userId))
                .orderBy(NOTE.TOP.desc(), NOTE.UPDATE_TIME.desc());

//        查询用户的笔记列表
        List<Note> notes = null;
        try {
            notes = noteDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("获取笔记失败", EventCode.SELECT_EXCEPTION);
        }

//        将笔记列表返回给调用者
        return notes;
    }
}
