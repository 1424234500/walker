package com.walker.core.exception;

/**
 * 作为Objects扩展
 */
public class ErrorUtil {

    /**
     * 包装异常
     */
    public static void build(Exception e) {
        throw new RuntimeException(e);
    }



}
