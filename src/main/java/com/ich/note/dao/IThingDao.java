package com.ich.note.dao;

import com.ich.note.pojo.Thing;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IThingDao extends BaseMapper<Thing> {
}
