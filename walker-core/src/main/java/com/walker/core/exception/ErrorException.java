package com.walker.core.exception;

import com.walker.core.mode.Error;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 场景:
 * 初始化redis连接池
 * 正常测试获取连接成功
 * 异常则抛出信息 调用方捕获处理  是切换主备或者中止程序
 * <p>
 * 异常抛出时 程序中断 不需处理
 * <p>
 * eg:
 * redis启动异常
 *
 * @author walker
 */
@Data
@Accessors(chain = true)
public class ErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ErrorException(String message) {
        super(message);
        error = new Error(Error.LEVEL_ERROR, "exception", message);
    }

    public ErrorException(String message, Throwable cause) {
        super(message, cause);
        error = new Error(Error.LEVEL_ERROR, "exception", message);
    }

    public ErrorException(Throwable cause) {
        super(cause);
        error = new Error(Error.LEVEL_ERROR, "exception", cause.getMessage());
    }

    Error error;


}