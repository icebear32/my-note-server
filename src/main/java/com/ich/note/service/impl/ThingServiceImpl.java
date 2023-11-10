package com.ich.note.service.impl;

import com.ich.note.dao.INoteThingLogDao;
import com.ich.note.dao.IThingDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ServiceRollbackException;
import com.ich.note.pojo.NoteThingLog;
import com.ich.note.pojo.Thing;
import com.ich.note.service.INoteThingLogService;
import com.ich.note.service.IThingService;
import com.ich.note.util.EventCode;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.ich.note.pojo.table.Tables.THING;

import java.util.Date;
import java.util.List;

/**
 * @className: IThingServiceImpl
 * @Description: 小记业务的实现层
 * @Author: ich
 */
@Service
@Transactional(rollbackFor = {ServiceRollbackException.class})
public class ThingServiceImpl implements IThingService {

    @Autowired
    private IThingDao thingDao; // 小记的数据库接口
    @Autowired
    private INoteThingLogService noteThingLogService;

    /**
     * 修改小记
     * @param thing 小记信息（title，tags，content，userId，finished，updateTime，top，id）
     * @throws ServiceException 业务异常
     */
    @Override
    public void updateThing(Thing thing) throws ServiceException {
//        修改小记的条件
        QueryWrapper wrapper = QueryWrapper.create()
                .where(THING.ID.eq(thing.getId()))
                .and(THING.USER_ID.eq(thing.getUserId()))
                .and(THING.STATUS.eq(1));

        Thing updateColumn = Thing.builder()
                .title(thing.getTitle())
                .tags(thing.getTags())
                .content(thing.getContent())
                .finished(thing.getFinished())
                .top(thing.getTop())
                .updateTime(thing.getUpdateTime())
                .build();

        int count = 0;
        try {
            count = thingDao.updateByQuery(updateColumn, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("修改失败", EventCode.UPDATE_EXCEPTION);
        }

        if (count != 1) {
            throw new ServiceRollbackException("修改失败", EventCode.UPDATE_ERROR);
        }

//        修改小记的日志
        NoteThingLog log = NoteThingLog.builder()
                .time(thing.getUpdateTime())
                .event(EventCode.THING_UPDATE_SUCCESS)
                .desc("修改小记")
                .thingId(thing.getId())
                .userId(thing.getUserId())
                .build();

//        新增笔记小记日志记录（修改业务）
        noteThingLogService.addOneLog(log, true);
    }

    /**
     * 获取编辑的小记信息
     *
     * @param thingId 小记编号
     * @param userId 用户编号
     * @return 小记对象
     * @throws ServiceException 业务异常
     */
    @Override
    public Thing getEditThing(int thingId, int userId) throws ServiceException {
//        封装查询条件
        QueryWrapper wrapper = QueryWrapper.create()
                .select(THING.TITLE, THING.TOP, THING.TAGS, THING.CONTENT)
                .where(THING.ID.eq(thingId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1));

        Thing thing = null;
        try {
            thing = thingDao.selectOneByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("查询小记服务器异常", EventCode.SELECT_EXCEPTION);
        }

//        判断小记是否存在
        if (thing == null) {
            throw new ServiceException("小记不存在，请刷新后再试", EventCode.SELECT_NONE);
        }

//        将查询的小记返回出去
        return thing;
    }

    /**
     * 新增小记
     *
     * @param thing 小记信息（title，tags，content，userId，finished，time，updateTime，top）
     * @throws ServiceException 业务异常
     */
    @Override
    public void newCreateThing(Thing thing) throws ServiceException {
//        新增小记
        int count = 0;
        try {
            count = thingDao.insert(thing);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("新增小记失败", EventCode.THING_CREATE_EXCEPTION);
        }

        if (count != 1) {
            throw new ServiceRollbackException("新增小记失败", EventCode.THING_CREATE_FAILED);
        }

//        新增小记的日志
        NoteThingLog log = NoteThingLog.builder()
                .time(thing.getUpdateTime())
                .event(EventCode.THING_CREATE_SUCCESS)
                .desc("新增小记")
                .thingId(thing.getId())
                .userId(thing.getUserId())
                .build();

//        新增笔记小记日志记录（新增业务）
        noteThingLogService.addOneLog(log, true);
    }

    /**
     * 根据编号删除小记（彻底删除）
     *
     * @param complete 是否彻底删除
     * @param thingId 小记编号
     * @param userId 用户编号
     * @param isRecycleBin 是否是回收站的操作
     * @throws ServiceException 业务异常
     */
    @Override
    public void deleteThingById(boolean complete, int thingId, int userId, boolean isRecycleBin) throws ServiceException {
//        默认为正常删除操作，并不是彻底删除，也不是回收站中的删除
        String desc = "删除小记";
        String event = EventCode.THING_DELETE_SUCCESS;
        int beforeStatus = 1; // 之前的状态
        int afterStatus = 0; // 删除之后的状态
        if (complete) {
            desc = "彻底删除小记";
            event = EventCode.THING_COMPLETE_DELETE_SUCCESS;
            afterStatus = -1;
            if (isRecycleBin) beforeStatus = 0; // 在回收站中的小记状态都是已删除的
        }

//        封装修改条件：WHERE `id` = ? AND `u_id` = ? AND `status` = ?(1/0)
        QueryWrapper wrapper = QueryWrapper.create()
                .where(THING.ID.eq(thingId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(beforeStatus));

//        要操作的时间
        Date localTime = new Date();

//        要修改的哪些字段：status
        Thing thing = Thing.builder()
                .status(afterStatus)
                .updateTime(localTime)
                .build();

        int count = 0;
        try {
//            调用修改语句（数据库接口）
            count = thingDao.updateByQuery(thing, wrapper);
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
                .thingId(thingId)
                .userId(userId)
                .build();

//        新增笔记小记日志记录（删除业务）
        noteThingLogService.addOneLog(log, true);
    }

    /**
     * 置顶小记（取消置顶小记）
     * @param isTop 是否置顶小记
     * @param thingId 小记编号
     * @param userId 用户编号
     * @throws ServiceException 业务异常
     */
    @Override
    public void topThing(boolean isTop, int thingId, int userId) throws ServiceException {
        String desc = "置顶小记"; // 事件描述
        String eventSuccess = EventCode.THING_TOP_SUCCESS; // 事件代码
        String eventFailed = EventCode.THING_TOP_FAILED; // 事件代码
        int beforeTop = 0; // 修改之前的 top 字段值
        int afterTop = 1; // 修改之后的 top 字段值
        if (!isTop) {
            desc = "取消置顶";
            eventSuccess = EventCode.THING_CANCEL_TOP_SUCCESS;
            eventFailed = EventCode.THING_CANCEL_TOP_FAILED;
            beforeTop = 1;
            afterTop = 0;
        }

//        封装修改的条件：WHERE `id` = ? AND `u_id` = ? AND `status` = 1 AND `top` = 1/0
        QueryWrapper wrapper = QueryWrapper.create()
                .where(THING.ID.eq(thingId))
                .and(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1))
                .and(THING.TOP.eq(beforeTop));

//        封装修改的字段 top
        Thing thing = Thing.builder().top(afterTop).build();

//        根据 wrapper 条件来修改小记的 top 字段
        int count = 0;
        try {
            count = thingDao.updateByQuery(thing, wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(desc + "小记异常", EventCode.UPDATE_EXCEPTION);
        }

        if (count != 1) {
            throw new ServiceRollbackException(desc + "小记异常", eventFailed);
        }

        Date localTime = new Date(); // 时间

//        新增小记日志记录（置顶业务）
        NoteThingLog log = NoteThingLog.builder()
                .time(localTime)
                .event(eventSuccess)
                .desc(desc)
                .thingId(thingId)
                .userId(userId)
                .build();

//        新增笔记小记日志记录（置顶业务）
        noteThingLogService.addOneLog(log, true);
    }

    /**
     * 获取用户正常的小记
     * @param userId 用户编号
     * @return 小记对象集合
     * @throws ServiceException 业务异常
     */
    @Override
    public List<Thing> getUserNormalThing(int userId) throws ServiceException {

        // WHERE `u_id` = ? AND `status` = 1 ORDER BY `finished`, `top` desc, `update_time` desc
        QueryWrapper wrapper = QueryWrapper.create()
                .select(THING.ID, THING.TITLE, THING.TOP, THING.TAGS, THING.UPDATE_TIME, THING.FINISHED)
                .where(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1))
                .orderBy(THING.FINISHED.asc(), THING.TOP.desc(), THING.UPDATE_TIME.desc());

        try {
            // 根据条件查询用户的小记
            return thingDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("小记列表服务器异常", EventCode.SELECT_EXCEPTION);
        }
    }
}
