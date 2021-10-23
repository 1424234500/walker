package com.walker.design.vall_mvc_beats;

public class BeatControllerDj implements BeatControllerInterface {
    BeatView beatView;
    BeatModelInterface beatModelInterface;

    public BeatModelInterface getBeatModelInterface() {
        return beatModelInterface;
    }

    public BeatControllerDj setBeatModelInterface(BeatModelInterface beatModelInterface) {
        this.beatModelInterface = beatModelInterface;
        return this;
    }

    @Override
    public void start() {
        log.info(getClass().getSimpleName() + " " + "start");
        beatView = new BeatView().setBeatControllerInterface(this).setBeatModelInterface(beatModelInterface);


        /**
         * 重载多接口 同实现类 无法区分 需显示转换
         */
        beatModelInterface.registerObserver((ObserverBPM) beatView);
        beatModelInterface.registerObserver((ObserverBeat) beatView);

        beatModelInterface.init();

        beatModelInterface.on();

    }

    @Override
    public void stop() {
        log.info(getClass().getSimpleName() + " " + "stop");
        beatModelInterface.off();

    }

    @Override
    public void increaseBPM(int deta) {
        log.info(getClass().getSimpleName() + " " + "increaseBPM" + " " + deta);

        beatModelInterface.setBPM(beatModelInterface.getBPM() + deta);
    }
}
