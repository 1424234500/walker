package com.walker.design.factory;


/**
 * 披萨店
 */
public class PizzaStoreNewyork extends PizzaStore {
    /**
     * 覆盖实现为chicago的披萨店
     */
    public IngredientFactory getIngredientFactory() {
        return new IngredientFactoryNewyork();
    }


}



