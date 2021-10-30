package com.walker.socket.server.job;

import com.walker.core.scheduler.TaskJob;
import com.walker.dubbo.DubboMgr;
import com.walker.mode.LogSocketModel;
import com.walker.service.LogService;
import com.walker.socket.server.plugin.aop.Model;
import com.walker.socket.server.plugin.aop.ModelCount;
import com.walker.socket.server.plugin.aop.ModelGroup;
import com.walker.system.Pc;
import com.walker.util.Bean;
import com.walker.util.TimeUtil;
import org.apache.log4j.Logger;

import java.util.Map;

public class JobQpsMinute extends TaskJob {


	public static String[] types = {"net", "wait", "done"};
	static Logger log = Logger.getLogger(JobQpsMinute.class);
	static Bean mapLastCount = new Bean();
	static Bean mapLastCountDeta = new Bean();
	static Bean mapLastTime = new Bean();
	static Long timeLast = 0L;

	static {
		for (String type : types) {
			mapLastCount.put(type, new Bean());
			mapLastCountDeta.put(type, new Bean());
			mapLastTime.put(type, new Bean());
		}
	}

	/**
	 * 计数器
	 * plugin1
	 * net
	 * count	1
	 * cost	998
	 * wait
	 * count
	 * cost
	 * done
	 * count
	 * cost
	 */
	@Override
	public String make() {

		Long temp = System.currentTimeMillis();
		Double detaTime = 1.0 * (temp - timeLast + 1) / 1000 + 1;    //单位秒   避免0.001s中1个则1000？ 至少配置1s存储间隔
		String timeStr = TimeUtil.getTime("yyyy-MM-dd HH:mm:ss");
		timeLast = temp;

//stat:net:messagePlugin	2019-12-12 n-qps 320  n-ave 29   w-qps 32  w-ave 32  d-qps 31 d-ave
//时间区间detaTime60s，次数增长count200次，按类型net wait done 按接口message login 计算调用qps
		ModelCount modelCount = ModelCount.getInstance();
		Map<String, ModelGroup> map = modelCount.getMap();
		for (Map.Entry<String, ModelGroup> entry : map.entrySet()) {

			String plugin = entry.getKey();
			LogSocketModel logSocketModel = new LogSocketModel()
					.setPLUGIN(plugin)
					.setIP_PORT(Pc.getIps().toString());


			ModelGroup modelGroup = entry.getValue();
			for (String type : types) {
				Model model = modelGroup.getModelMap().get(type);
				if (model == null) {
					model = new Model();
				}

				long count = model.getCount();
				long cost = model.getCost();
//				Long qps = (long) Math.ceil( 1.0 * count / detaTime);	//message	*net:qps:232
				Long ave = (long) Math.ceil(1.0 * cost / Math.max(count, 1));    //*net:ave:322ms
				cost = ave;

				if (types[0].equalsIgnoreCase(type)) {
					logSocketModel
							.setNET_COUNT(count + "")
							.setNET_COST(cost + "")
					;
				} else if (types[1].equalsIgnoreCase(type)) {
					logSocketModel
							.setWAIT_COUNT(count + "")
							.setWAIT_COST(cost + "")
					;
				} else {
					logSocketModel
							.setDONE_COUNT(count + "")
							.setDONE_COST(cost + "")
					;
				}

//				plugin:message
//				net:
//					count:	22
//					cost:	998
//					qps	?
//					ave	?
//				wait:
//					count:	22
//					cost:	998


			}

			LogService logService = DubboMgr.getService("logService");
			logService.saveLogSocketModel(logSocketModel);

			log.info(logSocketModel.toString());

		}
//		清空 是否 事务问题 回滚问题
		map.clear();


		return "ok" + temp;
	}


//	@Override
//	public String make() {
//		Redis.getInstance().doJedis(new Fun<Object>() {
//
//			@Override
//			public Object make(Jedis jedis) {
//				List<String> keys;
//
////				obj.incrBy("stat:time:net:" + plugin, detaNet);
////				obj.incrBy("stat:time:wait:" + plugin, detaWait);
////				obj.incrBy("stat:time:done:" + plugin, detaDone);
////				obj.incrBy("stat:count:net" + plugin, 1L);
////				obj.incrBy("stat:count:wait" + plugin, 1L);
////				obj.incrBy("stat:count:done" + plugin, 1L);
//
////stat:net:message	2019-12-12 n-qps 320  n-ave 29   w-qps 32  w-ave 32  d-qps 31 d-ave
//
//				Long temp = System.currentTimeMillis();
//				Double detaTime = 1.0 * (temp - timeLast + 1) / 1000 + 1;	//避免0.001s中1个则1000？ 至少配置1s存储间隔
//				String timeStr = TimeUtil.getTime("yyyy-MM-dd HH:mm:ss");
//
//				Bean typeBean = new Bean();
//
//				//时间区间detaTime60s，次数增长count200次，按类型net wait done 按接口message login 计算调用qps
//				for(String type : types) {	//net
//					Bean mapLastCountItem = (Bean) mapLastCount.get(type);
//					Bean mapLastCountDetaItem =  (Bean) mapLastCountDeta.get(type);
//					Bean mapLastTimeItem = (Bean) mapLastTime.get(type);
//
//					keys = new ArrayList<>(jedis.keys("stat:count:" + type + ":*"));
//					Collections.sort(keys);
//					for(String key : keys) {
//						String plugin = key.substring(key.lastIndexOf(":") + 1);	//message
//						Long before = mapLastCountItem.get(plugin, 0L);
//						Long after = Long.valueOf(jedis.get(key));
//						Long count = after - before;
//						mapLastCountDetaItem.set(plugin, count);
//						Long qps = (long) Math.ceil( 1.0 * count / detaTime);	//message	*net:qps:232
//						typeBean.put(plugin, typeBean.get(plugin, "")+ " " + type + " " + fill("qps " + qps));
//						mapLastCountItem.set(plugin, after);
//					}
//
//					//时间区间detaTime60s， 耗时增长cost4000， 调用次数count20， 按接口计算调用net wait do次数平均耗时
//					keys = new ArrayList<>(jedis.keys("stat:time:" + type + ":*"));
//					Collections.sort(keys);
//					for(String key : keys) {	//stat:time:net:message
//						String plugin = key.substring(key.lastIndexOf(":") + 1);	//message
//						Long before = mapLastTimeItem.get(plugin, 0L);
//						Long after = Long.valueOf(jedis.get(key));
//						Long cost = after - before;
//						Long count = mapLastCountDetaItem.get(plugin, 0L);
//						count = count <= 0 ? 1 : count;
//						Long ave = (long) Math.ceil(1.0 * cost / count);	//*net:ave:322ms
//						typeBean.put(plugin, typeBean.get(plugin, "") + " " + fill("ave " + ave));
//
//						mapLastTimeItem.set(plugin, after);
//					}
//				}
//
//
//				//stat:net:message	2019-12-12 n-qps 320  n-ave 29   w-qps 32  w-ave 32  d-qps 31 d-ave
//				for(Object obj : typeBean.keySet()) {
//					String plugin = obj.toString();
//					String value = typeBean.get(plugin, "");
////					jedis.lpush("stat:" + plugin, timeStr + " " + value);
////					选择zset 时间戳为score分数 便于区间分页查询
//					jedis.zadd("stat:statis:" + plugin, temp, timeStr + " " + value);
//					log.warn("stat:statis:" + plugin + " " + timeStr + " " + value);
//				}
//
//				timeLast = temp;
//
//				return null;
//			}
//
//			private String fill(String str) {
//				return Tools.fillStringBy(str, " ", 8, 1);
//			}
//
//		});
//
//		return "ok";
//	}

}
