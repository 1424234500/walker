package com.walker.design.factory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 原料工厂 chicago款式
 */
public class IngredientFactoryChicago implements IngredientFactory {
    private static final Logger log = LoggerFactory.getLogger(IngredientFactoryChicago.class);


    @Override
    public IngredientSauce createSauce() {
        return new IngredientSauceChicago();
    }

    @Override
    public IngredientDough createDough() {
        return new IngredientDoughChicago();
    }
}



