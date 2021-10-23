package com.walker.design.iterator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv) {

        MenuLaunch<String> menuLaunch = new MenuLaunch<>().setItems("1", 2, 3, 4);
        MenuBreakfast<String> menuBreakfast = new MenuBreakfast<>().setItems("6", 7, 8);


        Waitress<String> waitress = new Waitress<String>()
                .setMenuBreakfasts(menuBreakfast.createIterator()).setMenuLaunch(menuLaunch.createIterator());

        waitress.printMenu();

    }


}



