package com.ich.note.service.impl;

import com.ich.note.dao.IThingDao;
import com.ich.note.exception.ServiceException;
import com.ich.note.exception.ServiceRollbackException;
import com.ich.note.pojo.Thing;
import com.ich.note.service.IThingService;
import com.ich.note.util.EventCode;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.ich.note.pojo.table.Tables.THING;

import java.util.List;

/**
 * @className: IThingServiceImpl
 * @Description: 小记业务的实现层
 * @Author: ich
 */
@Service
@Transactional(rollbackFor = {ServiceRollbackException.class})
public class IThingServiceImpl implements IThingService {

    @Autowired
    private IThingDao thingDao; // 小记的数据库接口

    /**
     * 获取用户正常的小记
     * @param userId 用户编号
     * @return 小记对象集合
     * @throws ServiceException 业务异常
     */
    @Override
    public List<Thing> getUserNormalThing(int userId) throws ServiceException {

        // WHERE `u_id` = ? AND `status` = 1 ORDER BY `finished`, `top`, `update_time` desc
        QueryWrapper wrapper = QueryWrapper.create()
                .where(THING.USER_ID.eq(userId))
                .and(THING.STATUS.eq(1))
                .orderBy(THING.FINISHED.asc(), THING.TOP.asc(), THING.UPDATE_TIME.desc());

        try {
            // 根据条件查询用户的小记
            return thingDao.selectListByQuery(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("小记列表服务器异常", EventCode.SELECT_EXCEPTION);
        }
    }
}
