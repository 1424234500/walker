package com.walker.dao;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 配置抽象
 *
 *  ID VALUE   ABOUT   S_FLAG  S_MTIME
 *
 */
@Repository
public class RateLimiterDao implements Limiter{
    private static Logger log = LoggerFactory.getLogger(RateLimiterDao.class);

    @Autowired
    ConfigDao configDao;

    String CONF_ID = "ratelimit";

    /**
     * 为每个接口创建limiter
     * method粒度 全路径?
     *      com.walker.controller.RedisControl.getLocks()
     *      /redis/getLocks.do?xxx=asdf
     *
     * 前置匹配 ?   全路径匹配
     * 其他限制 all
     *
     *
     */
    private ConcurrentHashMap<String, RateLimiter> indexRateLimiters = new ConcurrentHashMap<>();
    /**
     * 从数据库初始化 预热
     * 避免集体失效 分布过期时间
     */
    public int reload(){
        int res = -1;

        return res;
    }


    /**
     *
     * 穿透？  等待锁超时怎么办
     *
     * 并发访问 等待获取数据库 加锁 等待多一些时间
     *
     * 配置缓存10小时 或者永不过期？ 等待变化时通知清理缓存
     * 并发访问配置时 多等待充足时间 等待其他进程线程查询 10s
     *
     */
    private RateLimiter get(String url, String userId){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("mapget");
        RateLimiter rateLimiter = indexRateLimiters.get(url);
        stopWatch.stop();
        if(rateLimiter == null){
//            初始化限流 默认限流 默认预热
            stopWatch.start("getCountConfig");
            double eachSecondCount = getCountConfig(url);
            stopWatch.stop();
            if(eachSecondCount > 0) {
                stopWatch.start("init");
                int warmup = configDao.get("com.walker.intercept.RateLimitInterceptor.default.warmupPeriod", 3);
                log.info("init ratelimiter " + url + " to " + eachSecondCount + " warmup " + warmup);
                rateLimiter = RateLimiter.create(eachSecondCount, warmup, TimeUnit.SECONDS);
                indexRateLimiters.put(url, rateLimiter);
                stopWatch.stop();
            }
        }else{
//        集群模式下 缓存和内存同步问题
//        比对
            stopWatch.start("getCountConfig");
            double eachSecondCount = getCountConfig(url);
            stopWatch.stop();
            stopWatch.start("update");
            if(eachSecondCount == 0){
                indexRateLimiters.remove(url);
                rateLimiter = null;
            } else if (Math.abs(eachSecondCount - rateLimiter.getRate()) > 0.5) { //差值小于  则不做重置
                log.info("reset ratelimiter " + rateLimiter + " to " + eachSecondCount);
                rateLimiter.setRate(eachSecondCount);
            }else{
//                log.info("reset ratelimiter but too small " + rateLimiter + " to " + eachSecondCount);
            }
            stopWatch.stop();
        }
        log.debug(stopWatch.prettyPrint());
        return rateLimiter;
    }

    /**
     * //
     * @param url   /comm/getColsMap.do
     * @return 0则不限制 正数则限制   返回default or 配置值 or 全局默认值
     */
    private double getCountConfig(String url) {
        int eachSecondCount = configDao.get(url, -1);
        if(eachSecondCount == -1) {    //-1    继承全局限制
            eachSecondCount = configDao.get("com.walker.intercept.RateLimitInterceptor.default.count", Integer.MAX_VALUE);
        }else if(eachSecondCount == 0 ){     //0   禁用

        }else if(eachSecondCount == -2){ //-2  不限制
            eachSecondCount = Integer.MAX_VALUE;
        }else{
            eachSecondCount = Integer.MAX_VALUE;
        }
        return eachSecondCount;
    }

    /**
     *
     * ratelimiter限流 配置见dml
     * @param url   /redis/getColsMap.do
     * @param userId
     * @return 返回空则ok 否则提示信息
     */
    public String tryAcquire(String url, String userId){
        String res = "";
        StopWatch stopWatch = new StopWatch();
//        stopWatch.start("get-all");
//        RateLimiter all = get(CONF_ID, userId, configDao.get(CONF_ID, 999999D) );
//        stopWatch.stop();
//        stopWatch.start("acquire-all");
//        if(all != null && all.tryAcquire(1, 0L, TimeUnit.MICROSECONDS)){
//            stopWatch.stop();
            stopWatch.start("get-self");
            RateLimiter rateLimiter = get(url, userId);
            stopWatch.stop();
            stopWatch.start("acquire-self");
            if(rateLimiter != null && rateLimiter.tryAcquire(1, 0L, TimeUnit.MICROSECONDS)){
                log.debug("ratelimiter ok " + url + " " + userId);
            }else{ //0 或者其他 标示禁用url
                res = "ratelimiter out of " + url + " " + userId + " self limit " + (rateLimiter != null ? rateLimiter.getRate() : 0) ;
                log.warn(res );
            }
//        }else{
//            res = "ratelimiter out of " + url + " " + userId + " " + CONF_ID + " all limit " + (all != null ? all.getRate() : 0);
//            log.error(res);
//        }
        stopWatch.stop();
        log.debug(stopWatch.prettyPrint());

        return res;
    }


}

/*

    方法细节
            create
    public static RateLimiter create(double permitsPerSecond)
根据指定的稳定吞吐率创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询）。
        The returned RateLimiter ensures that on average no more than permitsPerSecond are issued during any given second, with sustained requests being smoothly spread over each second. When the incoming request rate exceeds permitsPerSecond the rate limiter will release one permit every (1.0 / permitsPerSecond) seconds. When the rate limiter is unused, bursts of up to permitsPerSecond permits will be allowed, with subsequent requests being smoothly limited at the stable rate of permitsPerSecond.
        返回的RateLimiter 确保了在平均情况下，每秒发布的许可数不会超过permitsPerSecond，每秒钟会持续发送请求。当传入请求速率超过permitsPerSecond，速率限制器会每秒释放一个许可(1.0 / permitsPerSecond 这里是指设定了permitsPerSecond为1.0) 。当速率限制器闲置时，允许许可数暴增到permitsPerSecond，随后的请求会被平滑地限制在稳定速率permitsPerSecond中。
        参数:
        permitsPerSecond – 返回的RateLimiter的速率，意味着每秒有多少个许可变成有效。
        抛出:
        IllegalArgumentException – 如果permitsPerSecond为负数或者为0
        create
public static RateLimiter create(double permitsPerSecond,long warmupPeriod,TimeUnit unit)
        根据指定的稳定吞吐率和预热期来创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询），在这段预热时间内，RateLimiter每秒分配的许可数会平稳地增长直到预热期结束时达到其最大速率（只要存在足够请求数来使其饱和）。同样地，如果RateLimiter 在warmupPeriod时间内闲置不用，它将会逐步地返回冷却状态。也就是说，它会像它第一次被创建般经历同样的预热期。返回的RateLimiter 主要用于那些需要预热期的资源，这些资源实际上满足了请求（比如一个远程服务），而不是在稳定（最大）的速率下可以立即被访问的资源。返回的RateLimiter 在冷却状态下启动（即预热期将会紧跟着发生），并且如果被长期闲置不用，它将回到冷却状态。
        参数:
        permitsPerSecond – 返回的RateLimiter的速率，意味着每秒有多少个许可变成有效。
        warmupPeriod – 在这段时间内RateLimiter会增加它的速率，在抵达它的稳定速率或者最大速率之前
        unit – 参数warmupPeriod 的时间单位
        抛出:

        IllegalArgumentException – 如果permitsPerSecond为负数或者为0
        setRate
public final void setRate(double permitsPerSecond)
        更新RateLimite的稳定速率，参数permitsPerSecond 由构造RateLimiter的工厂方法提供。调用该方法后，当前限制线程不会被唤醒，因此他们不会注意到最新的速率；只有接下来的请求才会。需要注意的是，由于每次请求偿还了（通过等待，如果需要的话）上一次请求的开销，这意味着紧紧跟着的下一个请求不会被最新的速率影响到，在调用了setRate 之后；它会偿还上一次请求的开销，这个开销依赖于之前的速率。RateLimiter的行为在任何方式下都不会被改变，比如如果 RateLimiter 有20秒的预热期配置，在此方法被调用后它还是会进行20秒的预热。
        参数:
        permitsPerSecond – RateLimiter的新的稳定速率
        抛出:
        IllegalArgumentException – 如果permitsPerSecond为负数或者为0
        getRate
public final double getRate()
        返回RateLimiter 配置中的稳定速率，该速率单位是每秒多少许可数。它的初始值相当于构造这个RateLimiter的工厂方法中的参数permitsPerSecond ，并且只有在调用setRate(double)后才会被更新。
        acquire
public double acquire()
        从RateLimiter获取一个许可，该方法会被阻塞直到获取到请求。如果存在等待的情况的话，告诉调用者获取到该请求所需要的睡眠时间。该方法等同于acquire(1)。
        返回:
        time spent sleeping to enforce rate, in seconds; 0.0 if not rate-limited
        执行速率的所需要的睡眠时间，单位为妙；如果没有则返回0
        Since:
        16.0 (版本13.0没有返回值)
        acquire
public double acquire(int permits)
        从RateLimiter获取指定许可数，该方法会被阻塞直到获取到请求数。如果存在等待的情况的话，告诉调用者获取到这些请求数所需要的睡眠时间。
        参数:
        permits – 需要获取的许可数
        返回:
        执行速率的所需要的睡眠时间，单位为妙；如果没有则返回0
        抛出:
        IllegalArgumentException – 如果请求的许可数为负数或者为0
        Since:
        16.0 (版本13.0没有返回值)
        tryAcquire
public boolean tryAcquire(long timeout,TimeUnit unit)
        从RateLimiter获取许可如果该许可可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可的话，那么立即返回false（无需等待）。该方法等同于tryAcquire(1, timeout, unit)。
        参数:
        timeout – 等待许可的最大时间，负数以0处理
        unit – 参数timeout 的时间单位
        返回:
        true表示获取到许可，反之则是false
        抛出:
        IllegalArgumentException – 如果请求的许可数为负数或者为0

        tryAcquire
public boolean tryAcquire(int permits)
        从RateLimiter 获取许可数，如果该许可数可以在无延迟下的情况下立即获取得到的话。该方法等同于tryAcquire(permits, 0, anyUnit)。
        参数:
        permits – 需要获取的许可数
        返回:
        true表示获取到许可，反之则是false
        抛出:
        IllegalArgumentException – 如果请求的许可数为负数或者为0
        Since:
        14.0
        tryAcquire
public boolean tryAcquire()
        从RateLimiter 获取许可，如果该许可可以在无延迟下的情况下立即获取得到的话。
        该方法等同于tryAcquire(1)。
        返回:
        true表示获取到许可，反之则是false
        Since:
        14.0
        tryAcquire
public boolean tryAcquire(int permits,long timeout,TimeUnit unit)
        从RateLimiter 获取指定许可数如果该许可数可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可数的话，那么立即返回false （无需等待）。
        参数:
        permits – 需要获取的许可数
        timeout – 等待许可数的最大时间，负数以0处理
        unit – 参数timeout 的时间单位
        返回:
        true表示获取到许可，反之则是false
        抛出:
        IllegalArgumentException -如果请求的许可数为负数或者为0
        toString
public String toString()
        以下描述复制于java.lang.Object类。
        返回对象的字符表现形式。通常来讲，toString 方法返回一个“文本化呈现”对象的字符串。
        结果应该是一个简明但易于读懂的信息表达式。建议所有子类都重写该方法。
        toString 方法返回一个由实例的类名，字符’@’和以无符号十六进制表示的对象的哈希值组成的字符串。换句话说，该方法返回的字符串等同于：
        getClass().getName() + ‘@’ + Integer.toHexString(hashCode())
        重载:
        Object类的toString方法
        返回:
        对象的字符表现形式*/
