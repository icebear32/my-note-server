package com.ich.note.service.impl;

import com.ich.note.dao.INoteDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Note;
import com.ich.note.service.INoteService;
import com.ich.note.util.EventCode;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ich.note.pojo.table.Tables.NOTE;

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
