package com.walker.design.factory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 原料工厂 chicago款式
 */
public class IngredientFactoryNewyork implements IngredientFactory {
    private static final Logger log = LoggerFactory.getLogger(IngredientFactoryNewyork.class);


    @Override
    public IngredientSauce createSauce() {
        return new IngredientSauceNewyork();
    }

    @Override
    public IngredientDough createDough() {
        return new IngredientDoughNewyork();
    }
}



