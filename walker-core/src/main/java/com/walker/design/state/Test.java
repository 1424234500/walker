package com.walker.design.state;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(State.class);

    public static void main(String[] argv) {

        GumballMachine gumballMachine = new GumballMachine()
                .setCount(10);

        gumballMachine.insertQuarter();
        gumballMachine.insertQuarter();
        gumballMachine.ejectQuarter();
        gumballMachine.ejectQuarter();
        gumballMachine.insertQuarter();
        gumballMachine.turnCrank();
        gumballMachine.turnCrank();

        for (int i = 0; i < 10; i++) {
            log.info("-----" + i + "---------");
            gumballMachine.insertQuarter();
            gumballMachine.turnCrank();
        }

    }


}



