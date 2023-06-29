package com.walker.core.cache;

import com.walker.core.Context;
import com.walker.core.database.MemCache;
import com.walker.core.system.SystemConfig;
import com.walker.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * 初始化来源于 配置目录（conf）下所有的properties文件
 * 重写get 拦截优先取用 system 配置属性
 * 然后取缓存（来源于配置文件）
 *
 * 重写put修改缓存 同步修改文件
 *
 */
public class ConfigMgr extends MemCache {
	private static Logger log = LoggerFactory.getLogger(ConfigMgr.class);

	private ConfigMgr() {
        reload();

    }
	private static class SingletonFactory{
		private static ConfigMgr instanse;
		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			instanse = new ConfigMgr();
			instanse.reload();
		}
	}
	public static ConfigMgr getInstance() {
        return SingletonFactory.instanse;
	}

	@Override
	public <V> Cache<String> put(String key, V value) {
		// todo update config file && system
		SystemConfig.set(key, String.valueOf(value));
		Setting.saveProperty(key, String.valueOf(value));
		return super.put(key, value);
	}

	@Override
	public <V> V get(String key, V defaultValue) {
		V v;

		if(SystemConfig.exists(key)){
			v = SystemConfig.get(key, defaultValue);
			log.warn("system config key " + key + " value " + v + " not cache config file ");
		}else{
			v = super.get(key, defaultValue);
		}
		return v;
	}

	public Long reload(){
		synchronized (this) {
			clear();
			Map<String, Object> res = new LinkedHashMap<>();
			List<String> fff = new ArrayList<>();
			int k = 0;
			File dir = new File(Context.getPathConf());
			log.warn("\tconf dir " + dir.getAbsolutePath());
			for (File item : dir.listFiles()) {
				String path = item.getAbsolutePath();
				if (FileUtil.check(path) == 0 && path.endsWith(".properties")) {
					log.warn("--" + k++ + "--\tparse file cache " + path);
					Map<String, Object> map = Setting.getSetting(path);
					map.forEach((key, value) -> {
						Object v = SystemConfig.get(key, value);
						log.warn("  -  \t" + key + "\t" + "  -  " + value);
					});
					fff.add(path);
					res.putAll(map);
				} else {
					log.warn("--" + k++ + "--\tparse file skype " + path);
				}
			}
			this.putAll(res);
			log.info("##-- cache init " + " " + res.size());
		}
        return size();
	}
}
