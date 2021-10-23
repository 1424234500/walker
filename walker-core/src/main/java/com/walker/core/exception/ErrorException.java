package com.walker.core.exception;

import com.walker.util.Tools;

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
public class ErrorException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ErrorException(Exception e) {
        super(e);
    }

    public ErrorException(Object object) {
        super(String.valueOf(object));
    }

    public ErrorException(Object... objects) {
        super(Tools.objects2string(objects));
    }

}