package com.walker.socket.server.chat.plugin;

import com.walker.core.route.SubPub;
import com.walker.core.route.SubPubMgr;
import com.walker.dubbo.DubboMgr;
import com.walker.mode.sys.PushModel;
import com.walker.service.PushAgentService;
import com.walker.mode.sys.DataPublish;
import com.walker.socket.model.Msg;
import com.walker.socket.frame.Session;
import com.walker.socket.server.chat.handler.HandlerSessionArpListImpl;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 业务分类处理插件
 *
 */
public abstract class Plugin<MSGDATA> implements MsgDataClass<MSGDATA>{
    public Logger log = LoggerFactory.getLogger(getClass());

	final public static String KEY_LOGIN = "login";
	final public static String KEY_SERVICE= "messageService";
	final public static String KEY_MESSAGE = "message";
	final public static String KEY_OFFLINEMSG= "offlinemsg";

	final public static String KEY_ECHO = "echo";
	final public static String KEY_MONITOR = "monitor";
	final public static String KEY_SESSION = "session";
	final public static String KEY_EXCEPTION = "exception";

	
	
	
	/**
     * 路由 发布订阅
     * pub发布 key socket定向消息 或者 发布user频道消息
     * 不需要session
     */
    private SubPub<Msg, Session<?, Msg>> pub = SubPubMgr.getSubPub("msg_route_server", 0);


	Bean params;
	Plugin(Bean params){
		this.params = params;
	}


	/**
	 * 目标 多用户 逻辑传递  发一个少一个
	 * 如何实现多用户连接同一台socket 
	 * 如何实现同ip连接同一台socket
	 * 对于单聊
	 * 		目标用户只有单端 ! 
	 * 
	 * 对于群聊
	 * 		成员列表取出 替换为单聊
	 * 
	 * 向上传递 || 定向传递
	 *  ip路由为什么是向上传递 因为没有一个注册中心 所有局域网向中心注册? 模拟路由ip向上传递
	 * 	Redis作为路由器
	 * 
树形路由节点广播模式 
以ip段位分区 0,256  0,4
路由表 本地广播 向上传递

                                                           lv0
       
                            lv1.0                                                       lv1.1
        
    lv1.0.0[0,3]   lv1.0.1[4,7]     lv1.0.3[8,11]   lv1.0.4[12,15]      lv1.1.0[16,19]       lv1.1.1[20,23]
	 * 
	 *  线程安全问题
	 */
	public void publish(Msg msg) {
        log.info("publish " + msg.toString());
//		消息推送
        if(msg.getPlugin().equals(Plugin.KEY_MESSAGE) && msg.getRes() instanceof DataPublish) {
            DataPublish bean = (DataPublish) msg.getRes();
//			{type:message,data:{to:123,from:222,type:txt,body:hello} }
			PushModel pushModel = new PushModel()
					.setUSER_ID(msg.getToUserId().toString())
					.setTITLE(msg.getFromUser().getName() + " " + bean.getTitle())
					.setCONTENT(bean.getBody())
					.setEXT(bean.toString())
					.setS_MTIME(TimeUtil.getTimeYmdHms())
					;

			try {
				((PushAgentService)DubboMgr.getService("pushAgentService")).push(pushModel);
			}catch (Exception e){
//				pushAgentService = null;
				log.error(e.toString(), e);
			}
		}


		//单端在线 记录未命中目标 向上传递
		List<String> offUsers = new ArrayList<>();
		for(String to : msg.getToUserId()) {
            msg.setToUserId(Arrays.asList(to));
			
			List<Session<?, Msg>> onUsers = publish(to, msg);
			log.info("publish " + to + " on " + onUsers.size() );
			log.info("订阅列表");
			Tools.formatOut(onUsers);
			
			if(onUsers.size() <= 0) {
				offUsers.add(to);
			}
		}
		//存在未命中的目标 则向上传递
		if(offUsers.size() > 0) {
			Tools.out("未命中目标 向上传递", offUsers, msg);
		}
	
	}
	
	public List<Session<?, Msg>> publish(String channel, Msg msg) {
		return pub.publish(channel, msg);
	}
	public Session<?, Msg> getSession(String socketFrom, String userId){
		return HandlerSessionArpListImpl.getInstance().getSession(socketFrom, userId);
	}

    /**
     * 泛型转换分发处理数据
     */
	abstract void onData(BeanLinked pluginParams, Msg msg, MSGDATA msgdata);


}
