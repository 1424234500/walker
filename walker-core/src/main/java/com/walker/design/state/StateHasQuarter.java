package com.walker.design.state;

import java.util.Random;

public class StateHasQuarter extends State {
    GumballMachine gumballMachine;
    Random random = new Random(System.currentTimeMillis());

    public StateHasQuarter(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }


    @Override
    String getName() {
        return "已投币...";
    }

    @Override
    void ejectQuarter() {
//        super.ejectQuarter();
        log.info("退币");
        gumballMachine.setState(gumballMachine.getStateNoQuarter());
    }

    @Override
    void turnCrank() {
//        super.turnCrank();
        if (random.nextInt(10) == 0) {
            log.info("oops luckly 双倍制作");
            gumballMachine.setState(gumballMachine.getStateSoldDouble());
        } else {
            log.info("制作");
            gumballMachine.setState(gumballMachine.getStateSold());
        }
    }
}
