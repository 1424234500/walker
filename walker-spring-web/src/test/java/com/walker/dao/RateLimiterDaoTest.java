package com.walker.dao;

import com.walker.ApplicationTests;
import com.walker.core.util.ThreadUtil;
import com.walker.core.util.TimeUtil;
import com.walker.core.util.Tools;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

public class RateLimiterDaoTest extends ApplicationTests {

    @Autowired
    RateLimiterDao rateLimiterDao;
    @Test
    public void tryAcquire() {
        String url = "/file/download.do";
        String userId = "walker";
        long t = 1 * 1000;
        int arr[] = {1, 1000};
        ThreadUtil.sleep(3000);

        for(int i = 0; i < arr.length; i++) {
            int qps = arr[i];
            int cc = 0;
            long wt = (long) (1000f / qps);
            long stopt = System.currentTimeMillis() + t;
            for (; System.currentTimeMillis() < stopt; ) {
                String res = rateLimiterDao.tryAcquire(url, userId);
                Tools.out("qps:" + qps, TimeUtil.getTimeYmdHmss(), url, userId, cc++, "via", res);
                ThreadUtil.sleep(wt);
            }
            ThreadUtil.sleep(5000);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("time");
        for(int i = 0; i < 1000; i++){
            System.currentTimeMillis();
        }
        stopWatch.stop();
        Tools.out(stopWatch.prettyPrint());



    }
}