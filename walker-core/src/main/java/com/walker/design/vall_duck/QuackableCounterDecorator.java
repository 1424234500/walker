package com.walker.design.vall_duck;


import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 *  让鸭子叫 都能计数而不改源码
 *
 *      装饰者模式
 *
 */
public class QuackableCounterDecorator implements Quackable{

    static AtomicInteger count = new AtomicInteger(0);
    Quackable quackable;

    public static int getCount() {
        return count.get();
    }


    public Quackable getQuackable() {
        return quackable;
    }

    public QuackableCounterDecorator setQuackable(Quackable quackable) {
        this.quackable = quackable;
        return this;
    }

    /**
     * 抽象功能 鸭叫  呱呱
     */
    @Override
    public void quack() {
        quackable.quack();
        count.addAndGet(1);
        log.info("count " + count);
    }
}



