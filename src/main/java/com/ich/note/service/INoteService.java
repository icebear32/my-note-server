package com.ich.note.service;

import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Note;

import java.util.Date;
import java.util.List;

// 关于笔记的业务接口
public interface INoteService {

    /**
     * 保存正在编辑的笔记
     *
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @param title 笔记标题
     * @param body 笔记内容
     * @param content 笔记内容（完整，包括 title 和 body）
     * @return
     * @throws ServiceException 业务异常
     */
    Date saveEditingNote(int noteId, int userId, String title, String body, String content) throws ServiceException;

    /**
     * 获取编辑的笔记信息
     *
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @return 小记对象
     * @throws ServiceException 业务异常
     */
    Note getEditNote(int noteId, int userId) throws ServiceException;

    /**
     * 创建笔记（并且初始化笔记）
     * @param userId
     * @return userId 用户编号
     * @throws ServiceException 业务异常
     */
    int createNoteInit(int userId) throws ServiceException;

    /**
     * 根据编号删除笔记（彻底删除）
     *
     * @param complete 是否彻底删除
     * @param noteId 笔记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站的操作
     * @throws ServiceException 业务异常
     */
    void deleteNoteById(boolean complete, int noteId, int userId, boolean isRecycleBin) throws ServiceException;

    /**
     * 置顶笔记（取消置顶笔记）
     * @param isTop 是否置顶笔记
     * @param NoteId 笔记编号
     * @param userId 用户编号
     * @throws ServiceException 业务异常
     */
    void topNote(boolean isTop, int NoteId, int userId) throws ServiceException;

    /**
     * 获取用户正常的笔记
     * @param userId 用户编号
     * @return 笔记列表
     * @throws ServiceException 业务异常
     */
    List<Note> getUserNormalNotes(int userId) throws ServiceException;
}
