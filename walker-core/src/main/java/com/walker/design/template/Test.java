package com.walker.design.template;


import com.walker.design.factory.IngredientFactoryNewyork;

public class Test {

    public static void main(String[] argv){

        Pizza pizzaCheese = new PizzaCheese(new IngredientFactoryNewyork());
        pizzaCheese.prepare();

        Pizza pizzaClam = new PizzaClam(new IngredientFactoryNewyork());
        pizzaClam.prepare();


    }



}



