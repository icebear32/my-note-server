package com.ich.note.service;

import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Note;

import java.util.List;

// 关于笔记的业务接口
public interface INoteService {

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
