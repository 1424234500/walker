package com.walker.design.vall_duck;


/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *  抽象鸭子厂   带计数的实现
 *
 */
public class DuckFactoryCounting extends DuckFactoryAbstract{


    @Override
    public Quackable createDuckMallard() {
        return new QuackableCounterDecorator().setQuackable(new DuckMallard());
    }

    @Override
    public Quackable createDuckCall() {
        return new QuackableCounterDecorator().setQuackable(new DuckCall());
    }

    @Override
    public Quackable createDuckRedhead() {
        return new QuackableCounterDecorator().setQuackable(new DuckRedhead());
    }
}



