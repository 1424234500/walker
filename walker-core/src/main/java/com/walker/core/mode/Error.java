package com.walker.core.mode;

import lombok.Getter;

//@Data
//@Accessors(chain = true)
@Getter
public class Error {
    /**
     * 错误级别 参考 log
     */
    final String level;
    /**
     * 如参数异常 须修改参数后重试
     */
    public static final String LEVEL_INFO = "info";

    /**
     * 如违规请求 请不要再违规调用
     */
    public static final String LEVEL_WARN = "warn";
    /**
     * 如系统异常 请退步延时重试
     */
    public static final String LEVEL_ERROR = "error";


    /**
     * 错误码
     */
    final String code;
    /**
     * 错误信息
     */
    final String msg;

    public Error(String level, String code, String msg) {
        this.level = level;
        this.code = code;
        this.msg = msg;
    }
}
