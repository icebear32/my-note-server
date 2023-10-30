package com.ich.note.pojo;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName: NoteThingLog
 * @Description: 笔记小记日志类
 * @Author: ich
 */
@Table(value = "z_note_thing_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteThingLog {
    @Id(keyType = KeyType.Auto)
    private Integer id; // 日志编号
    private Date time; // 时间
    private String event; // 事件码
    private String desc; // 描述
    @Column("u_id")
    private Integer userId; // 日志对应的用户编号
    @Column("n_id")
    private Integer noteId; // 日志对应的用户编号
    @Column("t_id")
    private Integer thingId; // 日志对应的用户编号
}