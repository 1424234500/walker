package com.walker.core.database;

import com.walker.core.aop.ConnectorAdapter;
import com.walker.core.aop.FunArgsReturn;
import com.walker.core.cache.ConfigMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;

import java.util.HashSet;
import java.util.Set;

/**
 * redis 烧饼模式
 * 接入工具模型
 * eg: *test
 */
public class RedisSentinel extends ConnectorAdapter {
	private final static Logger log = LoggerFactory.getLogger(RedisSentinel.class);
	protected String host = Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_PORT + "," + Protocol.DEFAULT_HOST + ":" + Protocol.DEFAULT_SENTINEL_PORT;
	protected Integer maxTotal = 100;
	protected Integer maxIdle = 4;
	protected Long maxWaitMills = 1000L;
	protected Integer connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	protected Integer soTimeout = Protocol.DEFAULT_TIMEOUT;
	protected String password = "";
	protected String clientName = null;
	protected Integer database = 0;
	protected String masterName = Protocol.SENTINEL_MASTERS;
	private JedisSentinelPool jedisSentinelPool;

	/**
	 * 单例模式
	 */
	public static RedisSentinel getInstance() {
		return SingletonFactory.instance;
	}

	@Override
	public String info() {
		return this.toString();
	}

	@Override
	public Boolean check() throws Exception {
		String res = this.doJedis(jedis -> {
			jedis.set("test hello", "world");
			return jedis.get("test hello");
		});
		log.info("test res " + res);
		return super.check();
	}

	@Override
	public Boolean init() throws Exception {
		this.doJedis(null);
		return super.init();
	}

	@Override
	public Boolean uninit() throws Exception {
		if (this.jedisSentinelPool != null)
			this.jedisSentinelPool.close();
		return super.uninit();
	}

	/**
	 * 回调环绕执行redis 操作
	 */
	public <T> T doJedis(FunArgsReturn<Jedis, T> fun) {
		if (fun != null) {
			Jedis jedis = this.getJedisSentinelPool().getResource();
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
	private JedisSentinelPool getJedisSentinelPool() {
		if (this.jedisSentinelPool == null) {
			synchronized (this) {
				if (this.jedisSentinelPool == null) {
					log.info("init begin " + this);
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(this.maxTotal);// 设置最大连接数
					poolConfig.setMaxWaitMillis(this.maxWaitMills);
					poolConfig.setMaxIdle(this.maxIdle);
					poolConfig.setTestOnBorrow(false);
					poolConfig.setTestOnReturn(false);
					poolConfig.setTestOnCreate(true);
					poolConfig.setBlockWhenExhausted(true);

					Set<String> sentinels = new HashSet<>();
					String[] nodes = this.host.split(",");
					for (int i = 0; i < nodes.length; i++) {        //集群结点
						log.info("RedisSentinel add node \t" + i + "\t " + nodes[i]);
						sentinels.add(nodes[i]);
					}

					this.jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, poolConfig, connectionTimeout, soTimeout, password, database, clientName);
					log.info("init ok " + this);
				}
			}
		}
		return this.jedisSentinelPool;
	}

	@Override
	public String toString() {
		return "RedisSentinel{" +
				"host='" + host + '\'' +
				", maxTotal=" + maxTotal +
				", maxIdle=" + maxIdle +
				", maxWaitMills=" + maxWaitMills +
				", connectionTimeout=" + connectionTimeout +
				", soTimeout=" + soTimeout +
				", password='" + password + '\'' +
				", clientName='" + clientName + '\'' +
				", database=" + database +
				", masterName='" + masterName + '\'' +
				'}';
	}

	public RedisSentinel setSoTimeout(Integer soTimeout) {
		this.soTimeout = soTimeout;
		return this;
	}

	public RedisSentinel setMasterName(String masterName) {
		this.masterName = masterName;
		return this;
	}

	public RedisSentinel setHost(String host) {
		this.host = host;
		return this;
	}

	public RedisSentinel setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
		return this;
	}

	public RedisSentinel setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
		return this;
	}

	public RedisSentinel setMaxWaitMills(Long maxWaitMills) {
		this.maxWaitMills = maxWaitMills;
		return this;
	}

	public RedisSentinel setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	public RedisSentinel setPassword(String password) {
		this.password = password;
		return this;
	}

	public RedisSentinel setClientName(String clientName) {
		this.clientName = clientName;
		return this;
	}

	public RedisSentinel setDatabase(Integer database) {
		this.database = database;
		return this;
	}

	private static class SingletonFactory {
		static RedisSentinel instance;

		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			ConfigMgr cache = ConfigMgr.getInstance();

			instance = new RedisSentinel();
			instance
					.setHost(cache.get("RedisSentinel.Host", instance.host))
					.setMasterName(cache.get("RedisSentinel.MasterName", instance.masterName))

					.setConnectionTimeout(cache.get("Redis.ConnectionTimeout", instance.connectionTimeout))
					.setMaxIdle(cache.get("Redis.MaxIdle", instance.maxIdle))
					.setMaxTotal(cache.get("Redis.MaxTotal", instance.maxTotal))
					.setMaxWaitMills(cache.get("Redis.MaxWaitMills", instance.maxWaitMills))
					.setSoTimeout(cache.get("Redis.SoTimeout", instance.soTimeout))
					.setClientName(cache.get("Redis.ClientName", instance.clientName))
					.setPassword(cache.get("Redis.Password", instance.password))
			;
		}
	}
}
