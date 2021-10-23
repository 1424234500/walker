package com.walker.design.state;

public class StateNoQuarter extends State {
    GumballMachine gumballMachine;

    public StateNoQuarter(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    String getName() {
        return "未投币...";
    }


    @Override
    void insertQuarter() {
//        super.insertQuarter();
        log.info("投币");
        gumballMachine.setState(gumballMachine.getStateHasQuarter());
    }

}
