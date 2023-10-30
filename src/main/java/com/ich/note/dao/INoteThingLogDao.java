package com.ich.note.dao;

import com.ich.note.pojo.NoteThingLog;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// 关于笔记小记日志的数据库接口
@Mapper
@Repository
public interface INoteThingLogDao extends BaseMapper<NoteThingLog> {
}
