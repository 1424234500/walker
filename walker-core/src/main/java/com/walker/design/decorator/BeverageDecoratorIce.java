package com.walker.design.decorator;

/**
 * 冰
 */
public class BeverageDecoratorIce extends BeverageDecorator{

    @Override
    public float costAdd() {
        return 2 * beverage.getSizePoint();
    }


}
