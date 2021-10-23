package com.walker.design.decorator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * 装饰者模式
 *
 * 装饰者模式        和父类做覆盖&组合操作 super.do + this.do
 *
 * is a 饮料
 *
 * 扩展
 *      咖啡
 *      啤酒
 *      奶茶
 *
 *
 */
public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv){


//* 买一杯（obj 底）
//*      咖啡
//*      奶茶
//*
//* 任意加（装饰）
//*      抹茶
//*      盐
//*      冰

        for(int size = 500; size < 800; size+=100) {

            Beverage tee = new BeverageTee().setSize(size);
            tee = new BeverageDecoratorSoy().setBeverage(tee);
            tee = new BeverageDecoratorIce().setBeverage(tee);
            tee = new BeverageDecoratorMocha().setBeverage(tee);
            log.info("cost:" + tee.cost());
            log.info("list:\n" + tee.description());

            Beverage caffe = new BeverageCaffe().setSize(size);
            caffe = new BeverageDecoratorIce().setBeverage(caffe);
            caffe = new BeverageDecoratorMocha().setBeverage(caffe);
            log.info("cost:" + caffe.cost());
            log.info("list:\n" + caffe.description());

        }
    }



}



