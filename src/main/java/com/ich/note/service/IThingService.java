package com.ich.note.service;

import com.ich.note.exception.ServiceException;
import com.ich.note.pojo.Thing;

import java.util.List;

// 业务接口：小记的业务
public interface IThingService {

    /**
     * 根据编号删除小记（彻底删除）
     *
     * @param complete 是否彻底删除
     * @param thingId 小记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站的操作
     * @throws ServiceException 业务异常
     */
    void deleteThingById(boolean complete, int thingId, int userId, boolean isRecycleBin) throws ServiceException;

    /**
     * 置顶小记（取消置顶小记）
     * @param isTop 是否置顶小记
     * @param thingId 小记编号
     * @param userId 用户编号
     * @throws ServiceException 业务异常
     */
    void topThing(boolean isTop, int thingId, int userId) throws ServiceException;

    /**
     * 获取用户正常的小记
     * @param userId 用户编号
     * @return 小记对象集合
     * @throws ServiceException 业务异常
     */
    List<Thing> getUserNormalThing(int userId) throws ServiceException;
}
