package com.walker.design.vall_mvc_beats;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 心跳
 */
public class BeatModelHeart implements BeatModelInterfaceHeart {
    /**
     * 节拍值
     */
    int bpm = 60;
    Set<ObserverBPM> setObserverBPM = new LinkedHashSet<>();


    /**
     * 获取心跳
     */
    @Override
    public int getHeartRate() {
        return this.bpm;
    }

    @Override
    public void setBPM(int bpm) {
        this.bpm = bpm;
        notifyObserverBPM();
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
            observerBPM.notifyBpm(getHeartRate());
        }
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " bpm." + getHeartRate()  ;
    }
}
