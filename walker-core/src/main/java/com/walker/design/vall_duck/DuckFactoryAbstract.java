package com.walker.design.vall_duck;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *  抽象鸭子厂
 *
 */
public abstract class DuckFactoryAbstract {
    Logger log = LoggerFactory.getLogger(DuckFactoryAbstract.class);

    public abstract Quackable createDuckMallard();
    public abstract Quackable createDuckCall();
    public abstract Quackable createDuckRedhead();



}



