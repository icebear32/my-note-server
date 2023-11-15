package com.ich.note.dao;

import com.ich.note.pojo.Note;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// 关于笔记的数据库接口
@Mapper
@Repository
public interface INoteDao extends BaseMapper<Note> {
}
