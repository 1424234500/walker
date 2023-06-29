package com.walker.core.service.rmi;

import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.core.service.service.ServiceClass;
import com.walker.core.util.ClassUtil;
import com.walker.core.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;


/**
 * Rmi 远程调用提供者 服务端 提供服务
 */
public class Provider extends TestAdapter {
	private static final Logger log = LoggerFactory.getLogger(Provider.class);
	private static final Map<String, Remote> map;

	static {
		map = new HashMap<>();
	}

	public static String getUrl() {
		if (map.size() > 0) {
			return map.keySet().iterator().next();
		}
		return "";
	}

	public boolean doInit() {
		log.info("********初始化远程调用 rmi*************");
		Cache<String> cache = ConfigMgr.getInstance();
		int port = cache.get("port_rmi", 8091);
		String clzs = cache.get("on_list_service", "");
		String[] clzss = clzs.split(",");

		//bind rmi端口
		try {
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}

		for (int i = 0; i < clzss.length; i++) {
			try {
				Remote obj = (Remote) ClassUtil.newInstance(clzss[i]);
				String name = obj.getClass().getSimpleName();
				if (name.endsWith("Impl")) {
					name = name.substring(0, name.length() - "Impl".length());
				}
				String url = "rmi://localhost:" + port + "/" + name;

				try {
					Naming.rebind(url, obj);
					map.put(url, obj);
					log.info("###publish.ok." + i + " " + url);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("###publish.error." + i + " " + url + " " + e);
				}
			} catch (Exception e) {
				return false;
			}
		}
		log.info("********启动远程服务完成 rmi over*************");

		log.info("--! 测试完毕 ------------------- ");
		return true;
	}

	public boolean doTest() {
		log.info("-- 开始测试WebService --------------");
		try {
			int port = ConfigMgr.getInstance().get("port_rmi", 8091);
			ServiceClass hello = (ServiceClass) Naming.lookup("rmi://localhost:" + port + "/ServiceClass");
			Tools.out(hello.test("hello rmi"));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("测试web service error !" + e);
			return false;
		}
		return true;
	}


}
