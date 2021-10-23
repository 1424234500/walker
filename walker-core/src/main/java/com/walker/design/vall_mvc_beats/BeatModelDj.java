package com.walker.design.vall_mvc_beats;

import java.util.LinkedHashSet;
import java.util.Set;

public class BeatModelDj implements BeatModelInterface {
    /**
     * 节拍值
     */
    int bpm = 90;
    boolean on = false;
    Set<ObserverBeat> setObserverBeat = new LinkedHashSet<>();
    Set<ObserverBPM> setObserverBPM = new LinkedHashSet<>();


    /**
     * 初始化
     */
    @Override
    public void init() {
        this.bpm = 90;
    }

    /**
     * 开
     */
    @Override
    public void on() {
        log.info(toString() + " on");
        on = true;
        notifyObserverBeat();
    }

    /**
     * 关
     */
    @Override
    public void off() {
        log.info(toString() + " off");
        on = false;
        notifyObserverBeat();
    }

    /**
     * 设置节奏
     *
     * @param bpm
     */
    @Override
    public void setBPM(int bpm) {
        this.bpm = bpm;
        notifyObserverBPM();
    }

    /**
     * 获取节奏状态
     *
     */
    @Override
    public int getBPM() {
        return this.bpm;
    }

    /**
     * 注册监听 每个节拍
     *
     * @param beatObserver
     */
    @Override
    public void registerObserver(ObserverBeat beatObserver) {
        this.setObserverBeat.add(beatObserver);
    }

    @Override
    public void unregisterObserver(ObserverBeat beatObserver) {
        this.setObserverBeat.remove(beatObserver);
    }

    /**
     * 注册监听 特殊节拍
     *
     * @param beatObserver
     */
    @Override
    public void registerObserver(ObserverBPM beatObserver) {
        this.setObserverBPM.add(beatObserver);
    }

    @Override
    public void unregisterObserver(ObserverBPM beatObserver) {
        this.setObserverBPM.remove(beatObserver);
    }

    @Override
    public void notifyObserverBPM() {
        for(ObserverBPM observerBPM : setObserverBPM){
            observerBPM.notifyBpm(getBPM());
        }
    }

    @Override
    public void notifyObserverBeat() {
        for(ObserverBeat observerBeat : setObserverBeat){
            observerBeat.notifyBeat(getBPM());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " bpm." + getBPM() + " on." + on;
    }
}
