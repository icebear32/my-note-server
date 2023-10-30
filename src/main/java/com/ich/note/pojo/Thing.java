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
 * @ClassName: Thing
 * @Description: 小记类
 * @Author: ich
 */
@Table(value = "z_thing")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Thing {
    @Id(keyType = KeyType.Auto)
    private Integer id; // 小记编号
    private String title; // 标题
    private String tags; // 标签
    private String content; // 内容
    @Column("u_id")
    private Integer userId; // 小记所属用户编号
    private Integer finished; // 是否已完成【0：未完成 1：已完成】
    private Date time; // 创建时间
    @Column("update_time")
    private Date updateTime; // 最后一次操作的时间
    private Integer top; // 是否置顶【1：置顶 0：不置顶】
    @Column(onInsertValue = "1")
    private Integer status; // 状态【0：删除 1：正常 -1：彻底删除】
    @Column(onInsertValue = "2")
    private Integer type; // 类别【1：笔记 2：小记】
}
