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
public interface Quackable {
    Logger log = LoggerFactory.getLogger(Quackable.class);

    /**
     * 抽象功能 鸭叫  呱呱
     */
    void quack();

}



