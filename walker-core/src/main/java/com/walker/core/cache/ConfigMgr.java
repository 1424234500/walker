package com.walker.core.cache;

import com.walker.core.Context;
import com.walker.core.database.MemCache;
import com.walker.setting.Setting;
import com.walker.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 基于缓存 db 构建配置中心
 * 仅用于 properties 配置 redis db 而非spring环境? 
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
		}
	}
	public static ConfigMgr getInstance() {
        SingletonFactory.instanse.reload();
        return SingletonFactory.instanse;
	}




	/**
	 * 1.加载file
     * 2.从数据库初始化 预热
	 * 避免timeout all
	 */
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
