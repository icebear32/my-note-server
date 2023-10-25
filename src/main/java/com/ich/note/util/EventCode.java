package com.ich.note.util;

/**
 * @className: EventCode
 * @Description: 事件状态码
 * @Author: ich
 */
public class EventCode {

//    ===== 登录状态码 =====
    public static  final String LOGIN_EMAIL_PASSWORD_SUCCESS = "L_001"; // 登录成功（邮箱和密码）
    public static  final String LOGIN_FAIL = "L_002"; // 登录失败
    public static  final String LOGIN_LOG_CREATE_EXCEPTION = "L_003"; // 登录日志创建异常
    public static  final String LOGIN_LOG_CREATE_FAIL = "L_004"; // 登录日志创建失败
    public static  final String LOGIN_SAVE_USER_TOKEN_REDIS_EXCEPTION = "L_005"; // 登录成功存储用户信息至 redis 失败

//    ===== SQL 业务状态码 =====
    public static  final String SELECT_SUCCESS = "S_001"; // 查询成功
    public static  final String SELECT_EXCEPTION = "S_002"; // 查询异常
    public static  final String SELECT_ERROR = "S_003"; // 查询错误
    public static  final String SELECT_NONE = "S_004"; // 未查到信息
    public static  final String INSERT_EXCEPTION = "S_005"; // 新增异常
    public static  final String INSERT_ERROR = "S_006"; // 新增错误

//    ===== 账号的状态码 =====
    public static  final String ACCOUNT_CLOCK = "A_001"; // 账号被锁定
    public static  final String ACCOUNT_EMAIL_WRONG = "A_002"; // 账号邮箱有误
    public static  final String ACCOUNT_EMAIL_PASSWORD = "A_003"; // 账号密码有误
    public static  final String ACCOUNT_EMAIL_REGISTERED = "A_004"; // 邮箱账号已被注册
    public static  final String ACCOUNT_EMAIL_REGISTER_SUCCESS = "A_005"; // 邮箱账号注册成功
    public static  final String ACCOUNT_EMAIL_REGISTER_LOG_EXCEPTION = "A_006"; // 邮箱账号注册日志异常
    public static  final String ACCOUNT_EMAIL_REGISTER_LOG_ERROR = "A_005"; // 邮箱账号注册日志错误


//    ===== 邮箱服务状态码 =====
    public static  final String EMAIL_SEND_VC_SUCCESS = "E_001"; // 邮箱验证码发送成功
    public static  final String EMAIL_SEND_VC_ERROR = "E_002"; // 邮箱验证码发送失败
    public static  final String EMAIL_SEND_VC_SAVE_REDIS_ERROR = "E_003"; // 邮箱验证码存储失败
    public static  final String EMAIL_SEND_INIT_PASSWORD_EXCEPTION = "E_004"; // 邮箱发送账号的初始密码失败
}
