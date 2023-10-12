package com.walker.box;

import com.walker.core.system.Pc;

import java.awt.*;
import java.awt.event.KeyEvent;


public class TaskPubgFire {
    public static void main(String[] argv) throws Exception {

        new TaskPubgFire().fire();
//        new TaskPcClick().fe();


    }

    private void fire() throws InterruptedException {
        Thread.sleep(4000);
        Point p = Pc.mouseGet();
        while (Math.abs(p.x - (Pc.mouseGet().x)) < 10) {
            Thread.sleep(50);
            Pc.keyClick(KeyEvent.VK_E, 50);
        }

    }


}