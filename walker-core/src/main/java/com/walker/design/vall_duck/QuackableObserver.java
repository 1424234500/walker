package com.walker.design.vall_duck;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 * 鸭叫观测者    订阅发布模式
 *
 */
public class QuackableObserver implements Observer {
    Logger log = LoggerFactory.getLogger(QuackableObserver.class);

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof Duck){
            log.info("on " + o + " args:" + arg);
        }else{
            log.info("unknow " + o + " args:" + arg);
        }
    }


}



