package com.walker.design.state;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 状态模式
 * <p>
 * 万能糖果机问题     封装基于状态的行为，并将行为委托到当前状态   允许对象内部状态改变时改变行为
 * 机器多种状态下不同的处理逻辑，扩展问题
 * <p>
 * <p>
 * 未投币的情况下，各个功能如何处理
 * <p>
 * 已投币的情况下，
 * <p>
 * 已出货的情况下，
 * <p>
 * 已缺货的情况下，
 */
public abstract class State {
    Logger log = LoggerFactory.getLogger(State.class);

    abstract String getName();

    /**
     * 投币
     */
    void insertQuarter() {
        log.info("" + getName() + " can't 投币");
    }

    /**
     * 退币
     */
    void ejectQuarter() {
        log.info("" + getName() + " can't 退币");
    }

    /**
     * 转动手柄
     */
    void turnCrank() {
        log.info("" + getName() + " can't 转动手柄");
    }

    /**
     * 吐出糖果
     */
    void dispense() {
        log.info("" + getName() + " can't 出货");
    }

}
