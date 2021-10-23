package com.walker.design.vall_duck;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 */
public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv){
        testTemplateDecoder();
        testFactory();
        testCombileAndIterator();
        testObserver();


    }
    static void testTemplateDecoder(){
        log.info("鸭 模板 复用");
        Quackable call = new DuckCall();
        Quackable mallard = new DuckMallard();
        Quackable readhead = new DuckRedhead();
        simulate(call);
        simulate(mallard);
        simulate(readhead);

        log.info("鸭子叫计数 装饰者");
        Quackable call1 = new QuackableCounterDecorator().setQuackable(new DuckCall());
        Quackable mallard1 = new QuackableCounterDecorator().setQuackable(new DuckMallard());
        Quackable readhead1 = new QuackableCounterDecorator().setQuackable(new DuckRedhead());
        simulate(call1);
        simulate(mallard1);
        simulate(readhead1);

        log.info("鹅怎么学鸭叫问题  适配器 桥接");
        Quackable gooseWhite = new GooseQuackableAdapter().setHonkable(new GooseWhite());
        Quackable gooseBlack = new GooseQuackableAdapter().setHonkable(new GooseBlack());
        simulate(gooseWhite);
        simulate(gooseBlack);

    }
    static void testFactory(){
        DuckFactoryAbstract duckFactoryAbstract = new DuckFactory();
        log.info("抽象工厂模式来生产鸭子 生产鸭子");
        simulate(duckFactoryAbstract);

        DuckFactoryAbstract duckFactoryAbstractCounting = new DuckFactoryCounting();
        log.info("抽象工厂模式来生产鸭子 生产带计数的鸭子");
        simulate(duckFactoryAbstractCounting);

    }

    static void testCombileAndIterator(){
        log.info("一群鸭子的鸭子如何管理   组合模是 & 迭代器模式");

        DuckFactoryAbstract duckFactoryAbstractCounting = new DuckFactoryCounting();

        DucksComponet ducksComponet = new DucksComponet()
                .add(duckFactoryAbstractCounting.createDuckCall())
                .add(duckFactoryAbstractCounting.createDuckCall())
                .add(duckFactoryAbstractCounting.createDuckCall())

                .add(new DucksComponet()
                    .add(duckFactoryAbstractCounting.createDuckCall())
                    .add(duckFactoryAbstractCounting.createDuckMallard())
                    .add(duckFactoryAbstractCounting.createDuckRedhead())
                )
        ;

                ;
        simulate(ducksComponet);

    }

    static void testObserver(){
        log.info("发布订阅模式 监控鸭子行为通知");
        DuckFactoryAbstract duckFactoryAbstract = new DuckFactory();
        DuckCall call = (DuckCall) duckFactoryAbstract.createDuckCall();
        DuckMallard mallard = (DuckMallard) duckFactoryAbstract.createDuckMallard();
        DuckRedhead readhead = (DuckRedhead) duckFactoryAbstract.createDuckRedhead();
        DucksComponet ducksComponet = new DucksComponet()
                .add(call)
                .add(new DucksComponet()
                        .add(call)
                        .add(readhead)
                )
            ;
        /**
         * 一个观察者 观察多个鸭子
         * or
         * 多个观察者 观察一个鸭子
         */
        QuackableObserver quackableObserver = new QuackableObserver();
        call.addObserver(quackableObserver);
        mallard.addObserver(quackableObserver);
        readhead.addObserver(quackableObserver);

        simulate(call);
        simulate(mallard);
        simulate(readhead);
        simulate(ducksComponet);

        readhead.deleteObserver(quackableObserver);
        mallard.deleteObserver(quackableObserver);
        ducksComponet.addObserver(quackableObserver);

        simulate(call);
        simulate(mallard);
        simulate(readhead);
        simulate(ducksComponet);

    }


    static void simulate(DuckFactoryAbstract duckFactoryAbstract){
        Quackable call = duckFactoryAbstract.createDuckCall();
        Quackable mallard = duckFactoryAbstract.createDuckMallard();
        Quackable readhead = duckFactoryAbstract.createDuckRedhead();
        simulate(call);
        simulate(mallard);
        simulate(readhead);
    }

    /**
     * 模拟器 叫
     */
    static void simulate(Quackable quackable){
        quackable.quack();
    }



}



