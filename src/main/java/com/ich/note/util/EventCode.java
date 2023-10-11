package com.ich.note.util;

/**
 * @className: EventCode
 * @Description: 事件状态码
 * @Author: ich
 */
public class EventCode {

    public static  final String LOGIN_SUCCESS = "L_001"; // 登录成功
    public static  final String LOGIN_FAIL = "L_002"; // 登录失败

//    ===== SQL 业务状态码 =====
    public static  final String SELECT_SUCCESS = "S_001"; // 查询成功
    public static  final String SELECT_EXCEPTION = "S_002"; // 查询异常
    public static  final String SELECT_ERROR = "S_003"; // 查询错误
    public static  final String SELECT_NONE = "S_004"; // 未查到信息

//    账号的状态码
    public static  final String ACCOUNT_CLOCK = "A_001"; // 账号被锁定
}
