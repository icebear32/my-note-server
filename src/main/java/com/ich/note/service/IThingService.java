package com.ich.note.service;

import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Thing;

import java.util.List;

// 业务接口：小记的业务
public interface IThingService {

    /**
     * 获取用户正常的小记
     * @param userId 用户编号
     * @return 小记对象集合
     * @throws ServiceException 业务异常
     */
    List<Thing> getUserNormalThing(int userId) throws ServiceException;
}
