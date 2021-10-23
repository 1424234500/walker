package com.walker.core.database;

import com.walker.core.Context;
import com.walker.core.aop.FunArgsReturn;
import com.walker.core.cache.CacheLocker;
import com.walker.core.cache.ConfigMgr;
import com.walker.setting.Setting;
import com.walker.util.FileUtil;
import com.walker.util.LangUtil;
import com.walker.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


/**
 * 基于缓存 db 构建配置中心
 * 仅用于 properties 配置 redis db 而非spring环境?
 * ID VALUE   ABOUT   S_FLAG  S_MTIME
 */
public class ConfigMgrDb {
	private static final Logger log = LoggerFactory.getLogger(ConfigMgr.class);

	CacheLocker<String> cacheLoader;
	/**
	 * 项目启动初始化 配置 缓存  后续redis db缓存配置
	 */
	private CacheLocker<String> cache;

	private ConfigMgrDb() {
		reload();

	}

	public static ConfigMgrDb getInstanceNoreload() { //循环依赖 单例空指针初始化问题?
//        SingletonFactory.instanse.reload();
		return SingletonFactory.instanse;
	}

	public static ConfigMgrDb getInstance() {
		SingletonFactory.instanse.reload();
		return SingletonFactory.instanse;
	}

	/**
	 * 1.加载file
	 * 2.从数据库初始化 预热
	 * 避免timeout all
	 */
	public Long reload() {
		if (cacheLoader == null) {
			synchronized (this) {
				if (cacheLoader == null) {
					cacheLoader = new MemCache();
					cache = cacheLoader;
					cache.initCacheFromDb(24 * 3600 * 1000L, 10 * 1000L, 3600 * 1000L, new FunArgsReturn<String, Map<String, Object>>() {
						@Override
						public Map<String, Object> make(String obj) {
							Map<String, Object> res = new LinkedHashMap<>();
							List<String> fff = new ArrayList<>();
							int k = 0;
							File dir = new File(Context.getPathConf());
							if (!dir.isDirectory()) {
								log.warn("\turl no exists " + dir.getAbsolutePath());
							} else {
								System.setProperty("path_conf", "conf");

								log.warn("\tconf dir " + dir.getAbsolutePath());
								for (File item : dir.listFiles()) {
									String path = item.getAbsolutePath();
									if (FileUtil.check(path) == 0 && path.endsWith(".properties")) {
										log.warn("--" + k++ + "--\tparse file cache " + path);
										Map<String, Object> map = Setting.getSetting(path);
										map.forEach((key, value) -> {
											log.warn("  -  \t" + key + "\t" + "  -  " + value);
										});
										fff.add(path);
										res.putAll(map);
									} else {
										log.warn("--" + k++ + "--\tparse file skype " + path);
									}
								}
							}
							res.put("files", fff);
							log.info("##-- cacheLoader init " + " " + res);
							return res;
						}
					});
				}
			}
		}
		return cache.size();
	}

	/**
	 * 穿透？  wait timeout?
	 * 并发访问 等待db lock wait
	 * wait notify clear
	 * 并发访问 wait more 等待其他进程10s
	 */
	public <T> T get(String key, T defaultValue) {
		return LangUtil.turn(cache.getCacheOrDb(key, 3600L, 8000L, cacheKey -> {
			T v = defaultValue;
			Map<String, Object> line = new Dao().findOne("select VALUE from W_SYS_CONFIG where ID=? and S_FLAG='1' ", key);
			if (line != null) {
				Object vv = line.get(key);
				v = LangUtil.turn(vv, defaultValue);
			}
			return v;
		}), defaultValue);
	}

	public Long set(String ID, String VALUE, String ABOUT, String S_FLAG, String S_MTIME) {
		return cache.setDbAndClearCache(ID, 3600 * 1000L, obj -> {
			Integer rr = 0;
			boolean isexist = false;
			Map<String, Object> line = new Dao().findOne("select * from W_SYS_CONFIG where ID=?  ", ID);
			if (line != null) {
				isexist = true;
			}
			if (line == null) {
				line = new HashMap<>();
			}
			isexist = true;
			if (VALUE != null) {
				line.put("VALUE", VALUE);
			}
			if (ABOUT != null) {
				line.put("ABOUT", ABOUT);
			}
			if (S_FLAG != null) {
				line.put("S_FLAG", S_FLAG);
			}
			if (S_MTIME != null) {
				line.put("S_MTIME", S_MTIME);
			} else {
				line.put("S_MTIME", TimeUtil.getTimeYmdHmss());
			}
			if (isexist) {
				rr = new Dao().executeSql("update W_SYS_CONFIG set ID=?,VALUE=?,ABOUT=?,S_FLAG=?,S_MTIME=? where ID=? ", line.get("ID"), line.get("VALUE"), line.get("ABOUT"), line.get("S_FLAG"), line.get("S_MTIME"), line.get("ID"));
			} else {
				rr = new Dao().executeSql("insert into W_SYS_CONFIG values(" + SqlUtil.makePosition("?", line.size()) + ")", line.get("ID"), line.get("VALUE"), line.get("ABOUT"), line.get("S_FLAG"), line.get("S_MTIME"));
			}
			return Long.valueOf(rr);
		});
	}

	private static class SingletonFactory {
		private static final ConfigMgrDb instanse;

		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			instanse = new ConfigMgrDb();
		}
	}
}
