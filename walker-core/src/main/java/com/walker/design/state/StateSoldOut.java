package com.walker.design.state;

public class StateSoldOut extends State {
    GumballMachine gumballMachine;

    public StateSoldOut(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }

    @Override
    String getName() {
        return "缺货中...";
    }

}
