package com.walker.service;


import com.walker.config.ShiroConfig;
import com.walker.core.encode.JsonUtil;
import com.walker.core.mode.Bean;
import com.walker.core.mode.school.User;
import com.walker.core.mode.sys.LogModel;
import com.walker.core.mode.sys.LogSocketModel;
import com.walker.core.system.Pc;
import com.walker.core.util.HttpBuilder;
import com.walker.core.util.ThreadUtil;
import com.walker.core.util.Tools;
import com.walker.dao.JdbcDao;
import com.walker.dubbo.DubboMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 模拟用户操作
 */
@Service("makeTestService")
public class MakeTestService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    LogService logService;
    @Autowired
    JdbcDao jdbcDao;
    @Autowired
    ShiroConfig shiroConfig;

    ExecutorService executorService = ThreadUtil.getExecutorServiceInstance(4);
    @Autowired
    private RedisTemplate redisTemplate;


    public long makeUrlTestRandomThread(long timeA, long timeD, int threadSize) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                makeUrlTestRandom(timeA, timeD, threadSize);
            }
        });
        return 0;
    }

    /**
     * 开启threadSize个线程，持续timeAll时间，每个线程访问间隔timeDeta
     */
    public long makeUrlTestRandom(long timeA, long timeD, int threadSize){
        final long start = System.currentTimeMillis();
        if(threadSize <= 0){
            threadSize = 1;
        }
        if(timeA <= 1000){
            timeA = 1000;
        }
        if(timeD <= 10){
            timeD = 10;
        }
        final long timeDeta = timeD;
        LogModel logModel = LogModel.getDefaultModel()
                .setCATE(Config.getCateTest())
                .setUSER("test")
                .setWAY("")
                .setURL("makeUrlTestRandom")
                .setARGS(new Bean().put("timeAll", timeA).put("timeDeta", timeDeta).put("threadSize", threadSize).toString())
                ;
        final long timeAll = timeA;
        log.info("start makeUrlTestRandom " + logModel);
        logModel = logService.saveLogModelNoTime(logModel);
        AtomicLong count = new AtomicLong(0);
        try{
            ExecutorService service = Executors.newFixedThreadPool(threadSize);
            for(int i = 0; i < threadSize; i++) {
                final int tno = i;
                service.execute(new Runnable() {
                    @Override
                    public void run() {
//                        登录
                        String token = shiroConfig.onlineUser(new User().setID("test" + tno).setPWD("test"));

                        List<Map<String, Object>> list = jdbcDao.findPageRand(100,
                                "select * from W_LOG_MODEL t where 1=1 " +
                                        "and t.WAY='GET' " +
                                        "and ( t.URL like '%find%' or t.URL like '%get%' ) " +
                                        "and t.CATE=? ", Config.getCateController() );
                        if(list.size() == 0){
                            log.error("no date to do");
                            return ;
                        }

                        while( ! Thread.interrupted()){
                            int i = (int) (Math.random() * list.size());
                            Map<String, Object> map = list.get(i);
                            String url = String.valueOf(map.get("URL"));    // /common/findPage.do
                            String argsStr = String.valueOf(map.get("ARGS"));// {"showNum":"8","_DATABASE_":"walker","_TABLE_NAME_":"W_LOG_MODEL","nowPage":"1","order":""}
                            try {
                                url = "http://127.0.0.1:8090" + url;
                                Bean args = JsonUtil.get(argsStr);
                                String res = new HttpBuilder(url, HttpBuilder.Type.GET)
                                        .setHeaders(new Bean().put("TOKEN", token))
                                        .setConnectTimeout(3000)
                                        .setRequestTimeout(3000)
                                        .setSocketTimeout(4000)
                                        .setData(args)
                                        .buildString()
                                ;
                                log.info("makeUrl: " + url + " res:" + res.substring(0, Math.min(500, res.length())));
                                count.addAndGet(1);
                            }catch (Exception e){
                                log.error(e.getMessage(), e);
                            }
                            if(System.currentTimeMillis() - start > timeAll){
                                break;
                            }
                            try {
                                Thread.sleep(timeDeta);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                        log.info("makeTest thread over");
                    }
                });
            }
            //        等待任务队列消费完毕
            service.shutdown();
            try{
//            超时控制 并打印剩下的队列
                service.awaitTermination(timeAll + 3000, TimeUnit.MILLISECONDS);
                log.info("===============================================");
                log.info("WARN await time out now shutdown now !! " +  (timeAll + 3000) );
                List<Runnable> last = service.shutdownNow();
                log.info("WARN shutdown now last queue:" + last.size());
                Tools.formatOut(last);
                log.info("===============================================");
            }catch (Exception e){
                e.printStackTrace();
            }

            logModel.setRES(count.toString());
        }catch (Exception e){
            logModel.setEXCEPTION(Tools.toString(e)).setIS_OK(Config.FALSE);
        }finally {
            logService.saveLogModel(logModel);
        }

        return count.get();


    }


    /**
     *  模拟socket插入监控数据行 W_LOG_SOCKET_MODEL
     *  按照函数 时间戳 曲线x6   调度触发采样点
     *
     * @param plugin    插件模拟名
     * @param pCount    count系数   0,2 -> 0,200
     * @param pCost     cost系数    0,2 -> 0,200
     * @param t         曲线周期T 3600s
     */
    public Object makeLogOneLine(String plugin, int pCount, int pCost, Long t)  {
        Bean res = new Bean();

//        时序投影到周期 映射到0~2pi
        Long time = System.currentTimeMillis() / 1000;  //s为单位
//        time % t : t = x : 2 * pi
//        x = 2 * pi * (time % t) / t
//        y [-1, 1] -> y [0, 2]
//        y = sin(x) + 2
        double x = 2 * Math.PI * (time % t * 1f / t);
        double dx = 2 * Math.PI / 6;    //6线 周期分离
        double y = 0;
        res.put("pCount", pCount).put("pCost", pCost).put("t", t).put("time", time).put("plugin", plugin).put("dx", dx).put("x", x);

        LogSocketModel logSocketModel = new LogSocketModel()
                .setPLUGIN(plugin)
                .setIP_PORT(Pc.getIps().toString())
                ;
        y = Math.sin( x ) + 2;
        logSocketModel.setNET_COUNT( (int)(y * pCount) + "");
        x += dx;
        y = Math.sin( x ) + 2;
        logSocketModel.setNET_COST( (int)(y * pCost) + "");

        x += dx;
        y = Math.sin( x ) + 2;
        logSocketModel.setWAIT_COUNT( (int)(y * pCount) + "");
        x += dx;
        y = Math.sin( x ) + 2;
        logSocketModel.setWAIT_COST( (int)(y * pCost) + "");


        x += dx;
        y = Math.sin( x ) + 2;
        logSocketModel.setDONE_COUNT( (int)(y * pCount) + "");
        x += dx;
        y = Math.sin( x ) + 2;
        logSocketModel.setDONE_COST( (int)(y * pCost) + "");

        LogService logService = DubboMgr.getService("logService");
        logService.saveLogSocketModel(logSocketModel);
        log.info(logSocketModel.toString());

        return res;
    }
}
