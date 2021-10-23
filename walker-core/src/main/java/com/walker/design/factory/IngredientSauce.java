package com.walker.design.factory;


/**
 * 原料 酱汁 sauce
 */
public abstract class IngredientSauce {

    protected String getName() {
        return getClass().getSimpleName();
    }


    @Override
    public String toString() {
        return getName();
    }
}


