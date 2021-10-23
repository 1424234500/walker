package com.walker.design.vall_mvc_beats;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 */
public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv) {
        test1();
        test2();


    }

    private static void test2() {
        log.info("heart------");

        BeatModelInterfaceHeart beatModelInterface = new BeatModelHeart();
        BeatControllerInterface beatControllerInterface = new BeatControllerHeart()
                .setBeatModelInterfaceHeart(beatModelInterface)
                ;

        beatControllerInterface.start();
        beatControllerInterface.increaseBPM(1);
        beatControllerInterface.increaseBPM(-1);
        beatControllerInterface.increaseBPM(10);
        beatControllerInterface.stop();
    }

    private static void test1() {
        log.info("dj------");
        BeatModelInterface beatModelInterface = new BeatModelDj();
        BeatControllerInterface beatControllerInterface = new BeatControllerDj()
                .setBeatModelInterface(beatModelInterface)
                ;

        beatControllerInterface.start();
        beatControllerInterface.increaseBPM(1);
        beatControllerInterface.increaseBPM(-1);
        beatControllerInterface.increaseBPM(10);
        beatControllerInterface.stop();
    }

}



