package com.walker.core.database;

import com.walker.mode.CacheModelRedis;
import com.walker.mode.Page;
import com.walker.util.LangUtil;
import com.walker.core.encode.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisUtil {
	private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

	/**
	 * 分页查询值
	 */
	public static List<CacheModelRedis> findPage(final Jedis jedis, final String pattern, final Page page) {
		List<CacheModelRedis> list = new ArrayList<>();
		Set<String> keysSet = jedis.keys(pattern);
		Iterator<String> keys = keysSet.iterator();
		page.setNum(keysSet.size());
		for (int i = page.getStart(); i < keysSet.size() && i < page.getStop(); i++) {
			String key = keys.next();
			CacheModelRedis res = new CacheModelRedis();
			res.setKEY(key);
			res.setEXISTS("true");

			String type = jedis.type(key);
			res.setTYPE(type);
			res.setTTL(jedis.ttl(key));
			Long len = -1L;
			Object value = null;
			if (type.equals("string")) {
				value = jedis.get(key);
				len = jedis.strlen(key);
			} else if (type.equals("list")) {
				len = jedis.llen(key);
				value = jedis.lrange(key, 0, len < 50 ? -1 : 50);
			} else if (type.equals("hash")) {
				value = jedis.hgetAll(key);
				len = jedis.hlen(key);
			} else if (type.equals("set")) {
				value = jedis.smembers(key);
				len = jedis.scard(key);
			} else if (type.equals("zset")) {
				value = jedis.zrange(key, 0, len < 50 ? -1 : 50);
				len = jedis.zcard(key);
			} else {
				value = "none";
			}
			res.setVALUE(value);
			res.setLEN(len);
			list.add(res);
		}
		return list;
	}

	/**
	 * 创建or更新
	 */
	public static Long save(Jedis jedis, String key, Object value) {
		Long res = 0L;
		if (jedis.exists(key)) {
			jedis.del(key);
		}
		if (value instanceof List) {
			List<?> list = (List<?>) value;
			if (list != null) {
				for (Object obj : list) {
					res += jedis.rpush(key, String.valueOf(obj));
				}
			}
		} else if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			if (map != null) {
				for (Map.Entry<?, ?> entry : map.entrySet()) {
					res += jedis.hset(key, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
				}
			}
		} else {
			jedis.set(key, String.valueOf(value));
			res += 1;
		}
		return res;
	}

	/**
	 * 删除
	 */
	public static Long delete(Jedis jedis, String key) {
		return jedis.del(key);
	}


	/**
	 * 设置 序列化
	 */
	public static <V> void set(Jedis jedis, String key, V value, Long timemillExpire) {
		jedis.set(key.getBytes(), SerializeUtil.serialize(value));
		if (timemillExpire > 0) {
			jedis.pexpire(key, timemillExpire);
		}
	}

	/**
	 * 获取对象 序列化
	 */
	public static <V> V get(Jedis jedis, String key, V defaultValue) {
		Object res = defaultValue;
		byte[] str = jedis.get(key.getBytes());
		if (str != null && str.length > 0) {
			res = SerializeUtil.deserialize(str);
		}
		return (V) res;
	}

	/**
	 * 键值序列化 获取对象 自动解析键值类型
	 */
	public static <V> V getMap(Jedis jedis, String key, String item, V defaultValue) {
		Object res = defaultValue;
		byte[] str = jedis.hget(key.getBytes(), item.getBytes());
		if (str != null && str.length > 0) {
			res = LangUtil.turn(str, defaultValue);
		}
		return (V) res;
	}

	/**
	 * 键值序列化 设置对象 自动解析键值类型
	 */
	public static <V> Long setMap(Jedis jedis, String key, String item, V value, Long timemillExpire) {
		Long res = 0L;
		if (value == null) {
			res = jedis.hdel(key, item);
		} else {
			res = jedis.hset(key, item, String.valueOf(value));
		}
		if (timemillExpire > 0) {
			jedis.expire(key, (int) Math.ceil(timemillExpire * 1d / 1000));
		}
		return res;
	}

	public static Long setMap(Jedis jedis, String key, Map<?, ?> map, int timemillExpire) {
		Long res = 0L;
		log.debug("setMap " + key + " " + map + " " + timemillExpire);
		for (Object item : map.keySet().toArray(new Objects[0])) {
			Object value = map.get(item);
			res += jedis.hset(String.valueOf(key), String.valueOf(item), String.valueOf(value));
		}
		if (timemillExpire > 0) {
			//后置设定过期时间
			jedis.expire(key, timemillExpire);
		}
		return res;
	}


	public static Boolean exists(Jedis jedis, String lockKey) throws Exception {
		return jedis.exists(lockKey);
	}

	public static Boolean compareAndSet(Jedis jedis, String lockKey, String value, Long timemillExpire) throws Exception {
		String res = jedis.set(lockKey, value, "NX", "PX", timemillExpire);
		return res != null && res.length() > 0 && res.equalsIgnoreCase("ok");
	}

	public static String compareAndGet(Jedis jedis, String lockKey) {
		return jedis.get(lockKey);
	}

	public static Boolean compareAndDelete(Jedis jedis, String lockKey, String value) throws Exception {
		Boolean res = false;
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(value));
		if (!String.valueOf(result).equalsIgnoreCase("0")) {
			return true;
		}
		return res;
	}
}
