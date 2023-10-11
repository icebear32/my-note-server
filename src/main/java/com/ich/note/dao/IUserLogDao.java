package com.ich.note.dao;

import com.ich.note.pojo.UserLog;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// 关于用户日志的数据库接口
@Mapper
@Repository
public interface IUserLogDao extends BaseMapper<UserLog> {
}
