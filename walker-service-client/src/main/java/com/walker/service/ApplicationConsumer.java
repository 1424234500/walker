package com.walker.service;

import com.walker.core.util.ThreadUtil;
import com.walker.core.util.Tools;
import com.walker.dubbo.DubboMgr;
import lombok.SneakyThrows;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApplicationConsumer {
    int count = 0;
    ApplicationConsumer() throws Exception {
        DubboMgr.getInstance().setDubboXml("dubbo-service-config.xml").start();
        EchoService service  = DubboMgr.getService("echoService");
        Tools.out("dubbo", service.echo("hello"));
//
        ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
        sch.scheduleAtFixedRate(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Tools.out("--------------" + count ++);

                EchoService service  = DubboMgr.getService("echoService");
                Tools.out("dubbo", service.echo("hello " + count));
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
        ThreadUtil.sleep(1000);
        sch.shutdownNow();

        System.exit(0);
    }

    public static void main(String[] argv) throws Exception {
        new ApplicationConsumer();
    }
}
