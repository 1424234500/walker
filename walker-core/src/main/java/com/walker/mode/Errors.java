package com.walker.mode;

/**
 * 通用异常信息表
 * 子项目可继承扩展异常类目
 */
public class Errors {
    public static Error INVALID_PARAM = new Error(Error.LEVEL_INFO, "INVALID_PARAM", "参数不对");


    public static Error ILLEGAL_REQUEST = new Error(Error.LEVEL_WARN, "ILLEGAL_REQUEST", "违规请求");

    public static Error SYSTEM_TIMEOUT = new Error(Error.LEVEL_ERROR, "SYSTEM_TIMEOUT", "处理超时");
    public static Error SYSTEM_EXCEPTION = new Error(Error.LEVEL_ERROR, "SYSTEM_EXCEPTION", "系统异常");


}
