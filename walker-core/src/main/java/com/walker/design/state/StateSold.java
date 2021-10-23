package com.walker.design.state;

public class StateSold extends State {
    GumballMachine gumballMachine;

    public StateSold(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    String getName() {
        return "制作中...";
    }

    @Override
    public void dispense() {
        log.warn("出货");
        gumballMachine.releaseBall();
        /**
         * 状态切换
         */
        if (gumballMachine.getCount() > 0) {
            gumballMachine.setState(gumballMachine.getStateNoQuarter());
        } else {
            gumballMachine.setState(gumballMachine.getStateSoldOut());
        }
    }
}