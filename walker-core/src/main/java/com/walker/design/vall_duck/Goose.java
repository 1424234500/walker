package com.walker.design.vall_duck;


/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 * 鹅模板
 *
 */
public abstract class Goose implements Honkable {

    /**
     * 抽象功能 鹅叫
     */
    @Override
    public void honk() {
        log.info(getClass().getSimpleName() + " honk");

    }


}



