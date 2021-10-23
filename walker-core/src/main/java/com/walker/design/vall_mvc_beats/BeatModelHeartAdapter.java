package com.walker.design.vall_mvc_beats;

/**
 * 心跳
 */
public class BeatModelHeartAdapter implements BeatModelInterface {
    BeatModelInterfaceHeart beatModelInterfaceHeart;

    public BeatModelInterfaceHeart getBeatModelInterfaceHeart() {
        return beatModelInterfaceHeart;
    }

    public BeatModelHeartAdapter setBeatModelInterfaceHeart(BeatModelInterfaceHeart beatModelInterfaceHeart) {
        this.beatModelInterfaceHeart = beatModelInterfaceHeart;
        return this;
    }

    /**
     * 初始化
     */
    @Override
    public void init() {

    }

    /**
     * 开
     */
    @Override
    public void on() {

    }

    /**
     * 关
     */
    @Override
    public void off() {

    }

    /**
     * 设置节奏
     *
     * @param bpm
     */
    @Override
    public void setBPM(int bpm) {

    }

    /**
     * 获取节奏状态
     */
    @Override
    public int getBPM() {
        log.info(getClass().getSimpleName() + " adapter heart to dj");
        return beatModelInterfaceHeart.getHeartRate();
    }

    /**
     * 注册监听 每个节拍
     *
     * @param beatObserver
     */
    @Override
    public void registerObserver(ObserverBeat beatObserver) {
    }

    @Override
    public void unregisterObserver(ObserverBeat beatObserver) {

    }

    /**
     * 注册监听 特殊节拍
     *
     * @param beatObserver
     */
    @Override
    public void registerObserver(ObserverBPM beatObserver) {
        beatModelInterfaceHeart.registerObserver(beatObserver);
    }

    @Override
    public void unregisterObserver(ObserverBPM beatObserver) {
        beatModelInterfaceHeart.unregisterObserver(beatObserver);
    }

    @Override
    public void notifyObserverBPM() {
        beatModelInterfaceHeart.notifyObserverBPM();
    }

    @Override
    public void notifyObserverBeat() {

    }
}
