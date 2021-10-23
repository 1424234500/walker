package com.walker.design.state;

/**
 * 双倍出货
 */
public class StateSoldDouble extends State {
    GumballMachine gumballMachine;

    public StateSoldDouble(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    String getName() {
        return "惊喜制作中...";
    }

    @Override
    public void dispense() {
        log.warn("惊喜出货0");
        gumballMachine.releaseBall();
        if (gumballMachine.getCount() > 0) {
            log.warn("惊喜出货1");
            gumballMachine.releaseBall();
            if (gumballMachine.getCount() > 0) {
                gumballMachine.setState(gumballMachine.getStateNoQuarter());
            } else {
                gumballMachine.setState(gumballMachine.getStateSoldOut());
            }
        } else {
            log.warn("惊喜出货1 oops 缺货了2333");
            gumballMachine.setState(gumballMachine.getStateSoldOut());
        }
    }
}