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
public interface BeatModelInterface {
    Logger log = LoggerFactory.getLogger(BeatModelInterface.class);

    /**
     * 初始化
     */
    void init();

    /**
     * 开
     */
    void on();

    /**
     * 关
     */
    void off();

    /**
     * 设置节奏
     * @param bpm
     */
    void setBPM(int bpm);
    /**
     * 获取节奏状态
     */
    int getBPM();

    /**
     * 注册监听 每个节拍
     */
    void registerObserver(ObserverBeat beatObserver);
    void unregisterObserver(ObserverBeat beatObserver);

    /**
     * 注册监听 特殊节拍
     */
    void registerObserver(ObserverBPM beatObserver);
    void unregisterObserver(ObserverBPM beatObserver);

    void notifyObserverBPM();
    void notifyObserverBeat();

}



