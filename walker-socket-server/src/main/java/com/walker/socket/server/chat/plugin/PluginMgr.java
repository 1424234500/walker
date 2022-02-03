package com.walker.socket.server.chat.plugin;


import com.alibaba.fastjson.JSON;
import com.walker.core.Context;
import com.walker.core.encode.JsonUtil;
import com.walker.core.scheduler.Scheduler;
import com.walker.core.scheduler.SchedulerMgr;
import com.walker.core.scheduler.Task;
import com.walker.mode.Msg;
import com.walker.socket.base.frame.SocketException;
import com.walker.socket.server.chat.plugin.aop.Aop;
import com.walker.mode.BeanLinked;
import com.walker.util.ClassUtil;
import com.walker.util.FileUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PluginMgr {
	private static Logger log = Logger.getLogger(PluginMgr.class);
	private PluginMgr() {
		init();
	}
	public static PluginMgr getInstance() {
		return SingletonFactory.instance;
	}
	//单例
	private static class SingletonFactory{
		static PluginMgr instance;
		static {
			try {
				instance = new PluginMgr();
			}catch (Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
//	login:{
//		class:util.plugin.Login,
//		on:false,
//		limit:200,
//	}
	private BeanLinked plugins;			//业务处理类
	private List<BeanLinked> aopsBefore;	//处理之前处理
	private List<BeanLinked> aopsAfter;	//处理之后处理 环绕
@SuppressWarnings("unchecked")
//	class	:	util.aop.SizeFilter,
//	on		:	true,
//	excludes	:	[
//		test
//	],
//	params	:	{
//		size	:	2048,
//	},

	void init() {
		String path = Context.getPathConf("plugin.json");
		String str = null;
		try {
			str = FileUtil.readByLines(path, null, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(new File(path).getAbsolutePath() + " not exists ");
		}
		log.warn("plugin mgr init file: " + path);
    	log.warn(str);

		BeanLinked bean = JsonUtil.get(str);
		plugins = ((BeanLinked)bean.get("plugins"));
		aopsBefore = (List<BeanLinked>)bean.get("before");
		aopsAfter = (List<BeanLinked>)bean.get("after");
//		插件限流控制初始化

//		初始化定时任务计算qps
		Scheduler sch = SchedulerMgr.getInstance();
		try {
			sch.add(new Task("job_sys_JobQpsMinute", "com.walker.socket.server.job.JobQpsMinute", "calc each minute qps job", "0 * * * * ?"));
//			sch.addChildToNode(new Task("util.socket.server_1.job.JobQpsHour", "calc each Hour qps job", "0 0 * * * ?"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 处理消息
	 */
	public void doMsg(Msg msg) throws SocketException {
		//before
		if(doAop(aopsBefore, msg)){
			this.doPlugin(msg);
			msg.setTimeServerSend(System.currentTimeMillis());
			//after
            doAop(aopsAfter, msg);
		}
	}
	/**
	 * 按类别处理 业务 plugin  存储 加工 发送socket
	 */
	public <MSGDATA> void doPlugin(Msg msg) throws SocketException {
		String type = msg.getPlugin();
		BeanLinked bean = (BeanLinked) plugins.get(type);
//		plugin 配置处理
		if(bean == null) {
			throw new SocketException("该插件不存在", type);
		}
		if( ! bean.get("on", true)) {
			throw new SocketException("该插件已经关闭", type);
		}
		int limit = bean.get("limit", 0);
		if(limit > 0) {
			throw new SocketException("该插件限流", type, limit);
		}
		String clz = bean.get("class", "");
		BeanLinked params = bean.get("params", new BeanLinked());

//		一次构造上下文 所有plugin公用
        Plugin<MSGDATA> plugin = (Plugin<MSGDATA>) ClassUtil.newInstance(clz, params);
        MSGDATA msgdata = JSON.parseObject(JSON.toJSONString(msg.getData()), plugin.getMsgDataClass());
		plugin.onData(params, msg, msgdata);
	}

	private Boolean doAop(final List<BeanLinked> aops, final Msg msg) throws SocketException {
		for(BeanLinked bean : aops) {
			if(!bean.get("on", false)) {
				log.warn(Arrays.toString(new String[]{"过滤器已关闭", bean.toString()}));
			}else {
				@SuppressWarnings("unchecked")
				List<String> excludes = (List<String>) bean.get("excludes");
				if(excludes != null && excludes.contains(msg.getPlugin())) {
//					log.debug(Arrays.toString(new String[]{"aop exclude", bean.toString(), msg.getType()}));
				}else {
//					log.debug(Arrays.toString(new String[]{"aop do", msg.getType(), bean.toString() }));
					BeanLinked params = (BeanLinked) bean.get("params");
					String clz = bean.get("class", "");
					@SuppressWarnings("unchecked")
                    Aop<Msg> aop = (Aop<Msg>) ClassUtil.newInstance(clz, params);

					aop.doAop(msg);  //有一个拦截器没通过返回异常 捕获并提示
				}
			}
			
		}
		return true;
	}


}
