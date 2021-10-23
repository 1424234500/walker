package com.walker.design.vall_duck;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 *
 */
public interface Honkable {
    Logger log = LoggerFactory.getLogger(Honkable.class);

    /**
     * 抽象功能 鹅叫  鹅鹅鹅
     */
    void honk();

}



