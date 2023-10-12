package com.walker.box;

import com.walker.core.system.Pc;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class TaskPcClick {
    public static void main(String[] argv) throws Exception {

//        new TaskPcClick().make();
        new TaskPcClick().clickE();
//        new TaskPcClick().fe();


    }

    private void clickE() throws InterruptedException {
        Thread.sleep(4000);
        Point p = Pc.mouseGet();
        while (Math.abs(p.x - (Pc.mouseGet().x)) < 10) {
            Thread.sleep(50);
            Pc.keyClick(KeyEvent.VK_E, 50);
        }

    }

    public void fe() throws InterruptedException {
        Thread.sleep(7000);
        for (int i = 0; i < 50; i++) {
            Pc.mouseMove(0, 350, 100);
            Thread.sleep(100);
            Pc.keyClick(KeyEvent.VK_E, 30);
            Thread.sleep(50);
            for (int jj = 0; jj < 6; jj++) {
                Pc.keyClick(KeyEvent.VK_D, 40);
                Thread.sleep(50);
            }
            Thread.sleep(1000);
            Pc.keyClick(KeyEvent.VK_E, 40);
            Thread.sleep(17000);
            Pc.mouseMove(0, -1600);
            Thread.sleep(1000);
            for (int jj = 0; jj < 50; jj++) {
                Pc.keyClick(KeyEvent.VK_E, 40);
                Thread.sleep(40);
            }
            Thread.sleep(1000);
        }
    }

    public void make() throws InterruptedException {

        Thread.sleep(7000);
        int d = 100;
        for (int i = 0; i < 1400; i++) {
            Pc.keyClick(KeyEvent.VK_E, 50);
            Thread.sleep(50);
            Pc.mouseMove(0, -d);
            Thread.sleep(450);
            Pc.mouseClick(MouseEvent.BUTTON1, 50);
            Thread.sleep(200);
            Pc.keyClick(KeyEvent.VK_E, 50);
            Pc.mouseMove(0, d);
            Thread.sleep(500);
        }
    }

}