package com.walker.box;

import com.walker.util.ThreadUtil;
import com.walker.util.Tools;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskThreadPieTest {

    @Test
    public void start() {


        List<String> ll = new ArrayList<>();
        for(int i = 0; i < 99; i++){
            ll.add("t" + i);
        }
        Iterator<String> it = ll.iterator();
        new TaskThreadPie(ll.size()){

            @Override
            public void onStartThread(int threadNo) {

                String task = ll.get(threadNo);
                Tools.out(threadNo, "run", task);

                ThreadUtil.sleep(10);
            }
        }
        .setDetail(false)
        .setSleepTimeSch(2000)
        .setTimemillesWait(30 * 1000)
        .setThreadSize(3)
        .start();
    }
    @Test
    public void start2() {

        List<String> ll = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            ll.add("t" + i);
        }
        Iterator<String> it = ll.iterator();
        new TaskThreadPie(ll.size()){

            @Override
            public void onStartThread(int threadNo) {

//                String task = ll.get(threadNo);
                if(! it.hasNext()){
                    Tools.out("it not has next", threadNo);
                    return;
                }
                String task = it.next();

                Tools.out(threadNo, "run", task);

                ThreadUtil.sleep(2000);
            }
            @Override
            public void onMoreAction(){
                for(int i = 0; i < 10; i ++) {
                    final int j = i;
                    getPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            Tools.out("more run", j);
                            ThreadUtil.sleep(1000);
                        }
                    });
                }
            }
        }
        .setDetail(false)
        .setSleepTimeSch(1000)
        .setTimemillesWait(10*1000)
        .setThreadSize(2)
        .setThreadSizeMax(6)
        .start();
    }

}