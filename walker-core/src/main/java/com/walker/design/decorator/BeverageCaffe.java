package com.walker.design.decorator;

/**
 * caffe
 */
public class BeverageCaffe extends Beverage{




    @Override
    public float cost() {
        return 10 * super.getSizePoint();
    }
}
