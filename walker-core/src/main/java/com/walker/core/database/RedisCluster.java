package com.walker.core.database;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.aop.ConnectorAdapter;
import com.walker.core.cache.ConfigMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * redis 集群模式
 * 接入工具模型
 * eg: *test
 */
public class RedisCluster extends ConnectorAdapter {
	private final static Logger log = LoggerFactory.getLogger(RedisCluster.class);
	protected String host = "localhost:7000,localhost:7001,localhost:7002,localhost:7003,localhost:7004,localhost:7005"; //redis.clients.jedis.Protocol.DEFAULT_HOST
	protected Integer maxTotal = 100;
	protected Integer maxIdle = 4;
	protected Long maxWaitMills = 1000L;
	protected Integer connectionTimeout = redis.clients.jedis.Protocol.DEFAULT_TIMEOUT;
	protected Integer soTimeout = 10000;
	protected Integer maxAttempts = 4;
	protected String password = null;
	protected String clientName = null;
	private JedisCluster jedisCluster;

	/**
	 * 单例模式
	 */
	public static RedisCluster getInstance() {
		return SingletonFactory.instance;
	}

	@Override
	public String info() {
		return this.toString();
	}

	@Override
	public Boolean check() throws Exception {
		String res = this.doJedis(jedisCluster -> {
			jedisCluster.set("test hello", "world");
			return jedisCluster.get("test hello");
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
		if (this.jedisCluster != null)
			this.jedisCluster.close();
		return super.uninit();
	}

	/**
	 * 回调环绕执行redis 操作
	 */
	public <T> T doJedis(FunArgsReturn<JedisCluster, T> fun) {
		if (fun != null) {
			return fun.make(this.getJedisCluster());
		}
		return null;
	}

	/**
	 * 对象内单例 迟加载 双重锁
	 */
	private JedisCluster getJedisCluster() {
		if (this.jedisCluster == null) {
			synchronized (this) {
				if (this.jedisCluster == null) {
					log.info("init begin " + this);
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(this.maxTotal);// 设置最大连接数
					poolConfig.setMaxWaitMillis(this.maxWaitMills); //设置最大等待时间
					poolConfig.setMaxIdle(this.maxIdle);// 设置空闲连接
					String[] nodes = this.host.split(",");
					Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
					for (int i = 0; i < nodes.length; i++) {        //集群结点
						HostAndPort hostAndPort = HostAndPort.parseString(nodes[i]);
						log.info("RedisCluster add cluster \t" + i + "\t " + hostAndPort);
						jedisClusterNode.add(hostAndPort);
					}
					this.jedisCluster = new JedisCluster(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, clientName, poolConfig);
					log.info("init ok " + this);
				}
			}
		}
		return this.jedisCluster;
	}

	@Override
	public String toString() {
		return "RedisCluster{" +
				"host='" + host + '\'' +
				", maxTotal=" + maxTotal +
				", maxIdle=" + maxIdle +
				", maxWaitMills=" + maxWaitMills +
				", connectionTimeout=" + connectionTimeout +
				", soTimeout=" + soTimeout +
				", maxAttempts=" + maxAttempts +
				", password='" + password + '\'' +
				", clientName='" + clientName + '\'' +
				'}';
	}

	public RedisCluster setHost(String host) {
		this.host = host;
		return this;
	}

	public RedisCluster setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
		return this;
	}

	public RedisCluster setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
		return this;
	}

	public RedisCluster setMaxWaitMills(Long maxWaitMills) {
		this.maxWaitMills = maxWaitMills;
		return this;
	}

	public RedisCluster setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	public RedisCluster setSoTimeout(Integer soTimeout) {
		this.soTimeout = soTimeout;
		return this;
	}

	public RedisCluster setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}

	public RedisCluster setPassword(String password) {
		this.password = password;
		return this;
	}

	public RedisCluster setClientName(String clientName) {
		this.clientName = clientName;
		return this;
	}

	private static class SingletonFactory {
		static RedisCluster instance;

		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			ConfigMgr cache = ConfigMgr.getInstance();

			instance = new RedisCluster();
			instance
					.setHost(cache.get("RedisCluster.Host", instance.host))
					.setMaxAttempts(cache.get("RedisCluster.MaxAttempts", instance.maxAttempts))

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
