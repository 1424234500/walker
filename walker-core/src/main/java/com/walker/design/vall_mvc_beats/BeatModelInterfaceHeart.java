package com.walker.design.vall_mvc_beats;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      dj 控制器 mvc模式
 *      调整节奏 展示状态
 *
 *  model
 *
 */
public interface BeatModelInterfaceHeart {
    Logger log = LoggerFactory.getLogger(BeatModelInterfaceHeart.class);

    /**
     * 获取心跳
     */
    int getHeartRate();
    void setBPM(int bpm);
    /**
     * 注册监听
     */
    void registerObserver(ObserverBPM beatObserver);
    void unregisterObserver(ObserverBPM beatObserver);
    void notifyObserverBPM();

}



