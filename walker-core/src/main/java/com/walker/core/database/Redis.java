package com.walker.core.database;

import com.walker.core.cache.ConfigMgr;
import com.walker.core.cache.LockHelpRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

/**
 * redis 普通模式
 * 接入工具模型
 * CacheLockerBase
 * is a TestAdapter, cache, cacheDb, locker, lockerHelp
 * <p>
 * eg: *test
 */
public class Redis extends LockHelpRedis {
    private final static Logger log = LoggerFactory.getLogger(Redis.class);
    protected String host = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT;
    protected Integer maxTotal = 100;
    protected Integer maxIdle = 4;
    protected Long maxWaitMills = 1000L;
    protected Integer connectionTimeout = Protocol.DEFAULT_TIMEOUT;
    protected String password = null;
    protected String clientName = null;
    protected Integer database = 0;
    private JedisPool jedisPool;

    /**
     * 单例模式
     */
    public static Redis getInstance() {
        return SingletonFactory.instance;
    }

    @Override
    public String info() {
        return this.toString();
    }

    @Override
    public Boolean doTest() throws Exception {
        String res = this.doJedis(jedis -> {
            jedis.set("test hello", "world");
            return jedis.get("test hello");
        });
        log.info("test res " + res);
        return super.doTest();
    }

    @Override
    public Boolean doInit() throws Exception {
        this.doJedis(null);
        return super.doInit();
    }

    @Override
    public Boolean doUninit() throws Exception {
        if (this.jedisPool != null)
            this.jedisPool.close();
        return super.doUninit();
    }

    /**
     * 回调环绕执行redis 操作
     */
    public <T> T doJedis(Fun<T> fun) {
        if (fun != null) {
            Jedis jedis = this.getJedisPool().getResource();
            try {
                return fun.make(jedis);
            } finally {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 对象内单例 迟加载 双重锁
     */
    private JedisPool getJedisPool() {
        if (this.jedisPool == null) {
            synchronized (this) {
                if (this.jedisPool == null) {
                    log.info("init begin " + this);
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxTotal(this.maxTotal);// 设置最大连接数
                    poolConfig.setMaxWaitMillis(this.maxWaitMills); //设置最大等待时间
                    poolConfig.setMaxIdle(this.maxIdle);// 设置空闲连接
                    poolConfig.setTestOnBorrow(false);
                    poolConfig.setTestOnReturn(false);
                    poolConfig.setTestOnCreate(true);
                    poolConfig.setBlockWhenExhausted(true);
                    HostAndPort hostAndPort = HostAndPort.parseString(host);
                    this.jedisPool = new JedisPool(poolConfig, hostAndPort.getHost(), hostAndPort.getPort(), connectionTimeout, password, database, clientName);
                    log.info("init ok " + this);
                }
            }
        }
        return this.jedisPool;
    }

    @Override
    public String toString() {
        return "Redis{" +
                "host='" + host + '\'' +
                ", maxTotal=" + maxTotal +
                ", maxIdle=" + maxIdle +
                ", maxWaitMills=" + maxWaitMills +
                ", connectionTimeout=" + connectionTimeout +
                ", password='" + password + '\'' +
                ", clientName='" + clientName + '\'' +
                ", database=" + database +
                '}';
    }

    public Redis setHost(String host) {
        this.host = host;
        return this;
    }

    public Redis setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public Redis setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public Redis setMaxWaitMills(Long maxWaitMills) {
        this.maxWaitMills = maxWaitMills;
        return this;
    }

    public Redis setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Redis setPassword(String password) {
        this.password = password;
        return this;
    }

    public Redis setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public Redis setDatabase(Integer database) {
        this.database = database;
        return this;
    }

    public interface Fun<T> {
        T make(Jedis jedis);
    }

    private static class SingletonFactory {
        static Redis instance;

        static {
            ConfigMgr cache = ConfigMgr.getInstance();

            log.warn("singleton instance construct " + SingletonFactory.class);

            instance = new Redis();
            instance
                    .setHost(cache.get("Redis.Host", instance.host))


                    .setConnectionTimeout(cache.get("Redis.ConnectionTimeout", instance.connectionTimeout))
//                    .setMaxAttempts(cache.get("Redis.MaxAttempts", instance.maxAttempts))
                    .setMaxIdle(cache.get("Redis.MaxIdle", instance.maxIdle))
                    .setMaxTotal(cache.get("Redis.MaxTotal", instance.maxTotal))
                    .setMaxWaitMills(cache.get("Redis.MaxWaitMills", instance.maxWaitMills))
//                    .setSoTimeout(cache.get("Redis.SoTimeout", instance.soTimeout))
                    .setClientName(cache.get("Redis.ClientName", instance.clientName))
                    .setPassword(cache.get("Redis.Password", instance.password))
                    .setDatabase(cache.get("Redis.Database", instance.database))
            ;
        }
    }
}
