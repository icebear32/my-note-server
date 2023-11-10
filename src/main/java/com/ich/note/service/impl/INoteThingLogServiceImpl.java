package com.ich.note.service.impl;

import com.ich.note.dao.INoteThingLogDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ServiceRollbackException;
import com.ich.note.pojo.NoteThingLog;
import com.ich.note.service.INoteThingLogService;
import com.ich.note.util.EventCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @className: INoteThingLogServiceImpl
 * @Description: 笔记小记日志业务实现层
 * @Author: ich
 */
@Service
public class INoteThingLogServiceImpl implements INoteThingLogService {

    @Autowired
    private INoteThingLogDao noteThingLogDao; // 小记的数据库接口

    @Override
    public void addOneLog(NoteThingLog log, boolean isRollback) throws ServiceException {

        String message = log.getDesc() + "失败！";
        int count;

        try {
            count = noteThingLogDao.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
            if (isRollback) {
                throw new ServiceRollbackException(message, EventCode.LOG_CREATE_EXCEPTION);
            } else {
                throw new ServiceException(message, EventCode.LOG_CREATE_EXCEPTION);
            }
        }

        if (count != 1) {
            if (isRollback) {
                throw new ServiceRollbackException(message, EventCode.LOG_CREATE_ERROR);
            } else {
                throw new ServiceException(message, EventCode.LOG_CREATE_ERROR);
            }
        }
    }
}
