package com.walker.spring.config;


import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 自定义配置文件
 * <p>
 * 用@Configuration注解该类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
 */
@Configuration
@PropertySource({"classpath:make.properties"})
//@Component("makeConfig")
public class MakeConfig {

    /**
     * 全局变量读取设置
     */
    public static String TEST;


//    /**
//     * 造数 user 量级配置
//     */
//    @Value("${count.make.user:10}")
//    public int countMakeUser;

//    /**
//     * 同步 部分已同步 记录 过期时间 url:done 10d
//     */
//    @Value("${expire.url.done:864000}")
//    public Integer expireUrlDone;
//    /**
//     * 同步 锁过期时间 10h
//     */
//    @Value("${expire.lock.redis.sync.area:36000}")
//    public Integer expireLockRedisSyncArea;

//    @Value("${expire.lock.redis.make.user:3600}")
//    public Integer expireLockRedisMakeUser;
//    @Value("${expire.lock.redis.wait:500}")
//    public Integer expireLockRedisWait;


    //    @Value("${push.jpush.APP_KEY}")
//    public String pushJpushAppKey;
//
//    @Value("${push.jpush.MASTER_SECRET}")
//    public String pushJpushMasterSecret;
//    push.jpush.APP_KEY=
//    push.jpush.MASTER_SECRET=
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${test:testhello}")
    public String test;

    public MakeConfig() {
        log.info(Config.getPre() + "MakeConfig ");
//        log.info(Arrays.asList(pushJpushAppKey, pushJpushMasterSecret, test, TEST).toString());

    }

    @Value("${test}")
    public void setExamplePath(String test) {
        MakeConfig.TEST = test;
    }

}
