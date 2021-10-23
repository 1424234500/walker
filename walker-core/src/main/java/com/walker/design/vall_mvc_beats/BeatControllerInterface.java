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
 *
 *  controller
 */
public interface BeatControllerInterface {
    Logger log = LoggerFactory.getLogger(BeatControllerInterface.class);

    void start();
    void stop();
    void increaseBPM(int deta);

}



