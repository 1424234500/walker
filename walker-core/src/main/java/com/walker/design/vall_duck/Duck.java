package com.walker.design.vall_duck;


import java.util.Observable;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 * 鸭子模板
 *
 *      模板模式
 *
 */
public abstract class Duck extends Observable implements Quackable {

    /**
     * 抽象功能 叫   动物都会叫
     */
    @Override
    public void quack() {
        log.info(getClass().getSimpleName() + " quack");
        this.setChanged();
        this.notifyObservers(getClass().getSimpleName());
    }


}



