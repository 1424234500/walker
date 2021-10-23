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
 * view
 *
 *
 */
public class BeatView implements ObserverBeat,ObserverBPM{
    private static final Logger log = LoggerFactory.getLogger(BeatView.class);
    BeatControllerInterface beatControllerInterface;
    BeatModelInterface beatModelInterface;

    public BeatModelInterface getBeatModelInterface() {
        return beatModelInterface;
    }

    public BeatView setBeatModelInterface(BeatModelInterface beatModelInterface) {
        this.beatModelInterface = beatModelInterface;
        return this;
    }

    public BeatControllerInterface getBeatControllerInterface() {
        return beatControllerInterface;
    }

    public BeatView setBeatControllerInterface(BeatControllerInterface beatControllerInterface) {
        this.beatControllerInterface = beatControllerInterface;
        return this;
    }

    @Override
    public void notifyBpm(int bpm) {
        log.info(getClass().getSimpleName() + " observer view bpm " + bpm);

    }
    @Override
    public void notifyBeat(int bpm) {
        log.info(getClass().getSimpleName() + " observer view beat " + bpm);
    }

    /**
     * 页面响应  用户点击控制触发控制器
     * @param bpm
     */
    public void setBPM(int bpm){
        beatControllerInterface.increaseBPM(bpm);
    }


}



