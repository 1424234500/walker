package com.walker.design.state;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 状态模式
 * <p>
 * 万能糖果机问题
 * 机器多种状态下不同的处理逻辑，扩展问题
 * <p>
 * 未投币的情况下，各个功能如何处理
 * <p>
 * 已投币的情况下，
 * <p>
 * 已出货的情况下，
 * <p>
 * 已缺货的情况下，
 */
public class GumballMachine extends State {
    Logger log = LoggerFactory.getLogger(GumballMachine.class);
    State stateSoldOut;
    State stateNoQuarter;
    State stateHasQuarter;
    State stateSold;
    State stateSoldDouble;
    State state;
    AtomicInteger count = new AtomicInteger(0);

    public GumballMachine() {
        stateSold = new StateSold(this);
        stateSoldDouble = new StateSoldDouble(this);
        stateSoldOut = new StateSoldOut(this);
        stateNoQuarter = new StateNoQuarter(this);
        stateHasQuarter = new StateHasQuarter(this);

        setState(stateNoQuarter);
    }


    @Override
    String getName() {
        return "万能糖果机";
    }

    @Override
    public void insertQuarter() {
        state.insertQuarter();
    }

    @Override
    public void ejectQuarter() {
        state.ejectQuarter();
    }

    @Override
    public void turnCrank() {
        /**
         * 机器 按钮后自动出糖果 不需要用户手动控制dispense
         */
        state.turnCrank();
        state.dispense();
    }

    //    @Override
//    public void dispense() {
//    }
    public void releaseBall() {
        log.info("come! 这是你的糖果");
        this.count.addAndGet(-1);
    }

    /**
     * 报告 监控器
     */
    public String report() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(getName()).append("\n")
                .append("now is:" + state.getName()).append("\n")
        ;

        return stringBuilder.toString();
    }


    public State getStateSoldDouble() {
        return stateSoldDouble;
    }

    public GumballMachine setStateSoldDouble(State stateSoldDouble) {
        this.stateSoldDouble = stateSoldDouble;
        return this;
    }

    public State getState() {
        return state;
    }

    public GumballMachine setState(State state) {
        this.state = state;
        return this;
    }

    public int getCount() {
        return count.get();
    }

    public GumballMachine setCount(int count) {
        this.count.set(count);
        setState(getStateNoQuarter());
        return this;
    }

    public State getStateNoQuarter() {
        return stateNoQuarter;
    }

    public GumballMachine setStateNoQuarter(State stateNoQuarter) {
        this.stateNoQuarter = stateNoQuarter;
        return this;
    }

    public State getStateSoldOut() {
        return stateSoldOut;
    }

    public GumballMachine setStateSoldOut(State stateSoldOut) {
        this.stateSoldOut = stateSoldOut;
        return this;
    }

    public State getStateHasQuarter() {
        return stateHasQuarter;
    }

    public GumballMachine setStateHasQuarter(State stateHasQuarter) {
        this.stateHasQuarter = stateHasQuarter;
        return this;
    }

    public State getStateSold() {
        return stateSold;
    }

    public GumballMachine setStateSold(State stateSold) {
        this.stateSold = stateSold;
        return this;
    }


}
