package com.walker.design.factory;


/**
 * 披萨店
 */
public class PizzaStoreChicago extends PizzaStore {
    /**
     * 覆盖实现为chicago的披萨店
     */
    public IngredientFactory getIngredientFactory() {
        return new IngredientFactoryChicago();
    }


}



