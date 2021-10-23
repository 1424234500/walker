package com.walker.design.vall_mvc_beats;

public class BeatControllerHeart implements BeatControllerInterface {
    BeatView beatView;
    public BeatView getBeatView() {
        return beatView;
    }

    BeatModelInterfaceHeart beatModelInterfaceHeart;

    public BeatControllerHeart setBeatView(BeatView beatView) {
        this.beatView = beatView;
        return this;
    }

    public BeatModelInterfaceHeart getBeatModelInterfaceHeart() {
        return beatModelInterfaceHeart;
    }

    public BeatControllerHeart setBeatModelInterfaceHeart(BeatModelInterfaceHeart beatModelInterfaceHeart) {
        this.beatModelInterfaceHeart = beatModelInterfaceHeart;
        return this;
    }

    @Override
    public void start() {
        log.info(getClass().getSimpleName() + " " + "start");
        beatView = new BeatView()
                .setBeatControllerInterface(this)
                .setBeatModelInterface(
                    new BeatModelHeartAdapter()
                        .setBeatModelInterfaceHeart(beatModelInterfaceHeart)
                );

        /**
         * 重载多接口 同实现类 无法区分 需显示转换
         */
        beatModelInterfaceHeart.registerObserver((ObserverBPM) beatView);


    }

    @Override
    public void stop() {

    }

    @Override
    public void increaseBPM(int deta) {
        log.info(getClass().getSimpleName() + " " + "increaseBPM" + " " + deta);
        beatModelInterfaceHeart.setBPM(beatModelInterfaceHeart.getHeartRate() + deta);
    }

}
