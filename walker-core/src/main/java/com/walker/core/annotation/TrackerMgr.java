package com.walker.core.annotation;

import com.walker.core.aop.ConnectorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注解处理工厂
 * 负责初始化各种注解配置
 */

public class TrackerMgr extends ConnectorAdapter {
    static public Logger log = LoggerFactory.getLogger("Annotation");

    /**
     * 扫描所有注解 以及对应的处理器 并调用执行处理器 初始化注解系统
     */
    public boolean doInit() {
        log.info("**扫描所有注解 以及对应的处理器 并调用执行处理器 初始化注解系统");
        TrackerUtil.make("", TrackerUtil.scan(""));
        log.info("**!扫描所有注解 以及对应的处理器 并调用执行处理器 初始化注解系统");
        return true;
    }

    @Override
    public String info() {
        return null;
    }
}