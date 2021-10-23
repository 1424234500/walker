package com.walker.design.vall_duck;


/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *  抽象鸭子厂   普通实现
 *
 */
public class DuckFactory extends DuckFactoryAbstract{


    @Override
    public Quackable createDuckMallard() {
        return new DuckMallard();
    }

    @Override
    public Quackable createDuckCall() {
        return new DuckCall();
    }

    @Override
    public Quackable createDuckRedhead() {
        return new DuckRedhead();
    }
}



