package com.walker.mode;

public class Error {
    /**
     * 错误级别 参考 log
     */
    String level;
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
    String code = "";
    /**
     * 错误信息
     */
    String msg = "";


    public Error(String level, String code, String msg) {
        this.level = level;
        this.code = code;
        this.msg = msg;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
