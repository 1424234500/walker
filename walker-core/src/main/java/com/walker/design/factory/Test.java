package com.walker.design.factory;


public class Test {

    public static void main(String[] argv) {

        PizzaStore pizzaStoreNewyork = new PizzaStoreNewyork();

        PizzaStore pizzaStoreChicago = new PizzaStoreChicago();

        pizzaStoreNewyork.createPizza("cheese");
        pizzaStoreNewyork.createPizza("clam");

        pizzaStoreChicago.createPizza("cheese");
        pizzaStoreChicago.createPizza("clam");


    }


}



