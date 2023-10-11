package com.ich.note.dao;

import com.ich.note.pojo.User;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// 关于用户的数据库接口
@Mapper
@Repository
public interface IUserDao extends BaseMapper<User> {
}
