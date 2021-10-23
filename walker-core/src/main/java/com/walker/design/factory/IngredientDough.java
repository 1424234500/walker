package com.walker.design.factory;


/**
 * 原料 面团 dough
 */
public abstract class IngredientDough {

    protected String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getName();
    }
}



