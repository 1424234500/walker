package com.walker.socket.server.handler;

import com.walker.core.aop.Fun;
import com.walker.core.pipe.Pipe;
import com.walker.core.pipe.PipeMgr;
import com.walker.core.pipe.PipeMgr.Type;
import com.walker.mode.Key;
import com.walker.setting.Setting;
import com.walker.mode.Msg;
import com.walker.socket.base.MsgBuilder;
import com.walker.socket.base.Session;
import com.walker.socket.base.handler.HandlerAdapter;
import com.walker.socket.server.plugin.PluginMgr;
import com.walker.socket.server.plugin.aop.ModelCount;
import com.walker.util.Bean;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用戶连接管理
 * 策略：
 * key相同的session 是否需要覆盖socket对象 ？
 * 是否存在key相同 而 socket对不上 已销毁？
 * 
 * 启动pipe队列消费者线程
 * 		消费（plugin aop处理）
 * 			拦截
 * 			存储
 * 			发送
 * 			计数
 * 
 * 读取到socket消息 放入pipe队列
 * 
 * 
 */
public class HandlerSessionArpListImpl<SOCKET> extends HandlerAdapter<Session<SOCKET, Msg>, Msg> {
	private static Logger log = Logger.getLogger(HandlerSessionArpListImpl.class);
    /**
     * 私有静态内部类
     */
    private static class SingletonFactory{
        private static HandlerSessionArpListImpl instance;
        private static AtomicInteger count = new AtomicInteger(0);
        static {
            System.out.println("静态内部类初始化" + SingletonFactory.class + " count:" + count.addAndGet(1));
            instance = new HandlerSessionArpListImpl();
        }
    }
    /**
     * 内部类模式 可靠
     */
    public static HandlerSessionArpListImpl getInstance(){
        return SingletonFactory.instance;
    }


	/**
	 * 会话列表 Arp 	mac:<mac,ip>
	 * 可通过key session.send定向发送消息
	 * socket : session
	 */
    public Map<String, Session<SOCKET, Msg>> index = new ConcurrentHashMap<>();

    /**
     * 业务处理队列
     * 线程内部使用对象 避免再编码解码json
     * 多进程可切换使用redis 编码解码string
     */
//    private Pipe<Msg0> pipe = PipeMgr.getPipe(Type.PIPE, "queue-msg");
    private Pipe<String> pipe = PipeMgr.getPipe(Type.PIPE, "stat:queue-msg");

    public String show() {
    	String res = "\n------------show session - -------\n";
    	int i = 0;
    	for(Session<SOCKET, Msg> item : index.values()) {
    		res += i++ + "\t " + item.toString() + "\n";
    	}
    	res += "------------show session - -------\n";
    	return res;
    }

    @SuppressWarnings("unchecked")
    private HandlerSessionArpListImpl(){
    	PluginMgr.getInstance();//初始化任务
    	pipe.startConsumer(Setting.get("netty_thread_consumer", 1), new Fun<String>() {
			public void make(String msg1) {
				Msg msg = Msg.parse(msg1);
				ModelCount.getInstance().onWait(msg);
//				if(1==1)
//				return 0;
				Session<SOCKET, Msg> session = index.get(msg.getFromSocketKey()); //根据socket key找到session
				if(session != null) {
					NDC.push(session.toString());
					try {
						PluginMgr.getInstance().doMsg(msg);
					}catch(Exception e) {
						log.error("plugin exception", e);
						//插件处理异常 反馈异常
                        try {
                            sendServer(session, (Msg) MsgBuilder.makeException(msg, e));
                        }catch (Exception ee){
                            log.error(ee.getMessage(), ee);
                        }
					}
					NDC.pop();
				}else {
					log.error("该用户已不存在 " + msg.toString());
				}
//				ThreadUtil.sleep(50);
				ModelCount.getInstance().onDone(msg);

			}
    	});
    	
    }

    @Override
    public void onNewConnection(Session<SOCKET, Msg> socketSession) {
        String key = socketSession.key();
        Session<SOCKET, Msg> session = index.get(key);
        if(session != null) {
            log.error("addDataToParent userSocket have exist?" + key + " " + session);
        }else {
            socketSession.onConnect();
            index.put(key, socketSession);

            log.debug("addDataToParent userSocket " + session);
        }
    }

    @Override
    public void onDisConnection(Session<SOCKET, Msg> socketSession) {
        String key = socketSession.key();
        Session<SOCKET, Msg> session = index.get(key);
        if(session != null) {
            socketSession.onUnConnect();
            index.remove(key);
            log.debug("addDataToParent userSocket " + session);
        }else {
            log.error("remove no userSocket " + key + " " + index.get(key));
        }
    }

    @Override
    public void send(Session<SOCKET, Msg> socketSession, Msg msg) throws Exception {
        super.send(socketSession, msg);
    }

    @Override
    public void onReceive(Session<SOCKET, Msg> socketSession, Msg msg) throws Exception {
        String key = socketSession.key();
        Session<SOCKET, Msg> session = index.get(key);
        if(session != null) {
            //集成发送者的信息  禁止冒名顶替
            msg.setFromSocketKey(key);
            //设置userFrom当前用户 若消息包含了from 则不设置 允许顶替发消息
            msg.setFromUser(session.getUserSocket());
//            msg.setFromUserId(session.getUserSocket());
            //默认发给自己
            if(msg.getToUserId().size() == 0 && session.getUserSocket() != null) {
                msg.setToUserId(Arrays.asList(session.getUserSocket().getId()));
            }

            msg.setQueueWaitDepth(pipe.size());
//			pipe.set(msg);
            ModelCount.getInstance().onNet(msg);
            pipe.put(msg.toString());

        }else {//异常请求
            log.error("receive msg from no userSocket ? " + socketSession);
            msg.setData("receive msg from no userSocket ? " + socketSession);
            sendServer(socketSession, msg);
        }
    }

	public List<Bean> getSessionList() {
		List<Bean> res = new ArrayList<>();
		for(String key : index.keySet()) {
			Session<SOCKET, Msg> session = index.get(key);
			res.add(new Bean().set(Key.USER, session.getUserSocket()).set(Key.KEY, session.key()).set(Key.TIME, ""));
		}
		return res;
	}
	public Session<SOCKET, Msg> getSession(String socketKey, String userId) {
		Session<SOCKET, Msg> res = null;
		if(socketKey.length() > 0) {
			res = index.get(socketKey);
		}else if(userId.length() > 0) {
			for(String key : index.keySet()) {
				Session<SOCKET, Msg> session = index.get(key);
				if(session.getUserSocket() != null && session.getUserSocket().getId().equals(userId)) {
					res = session;
					break;
				}
			}
		}
		
		return res;
	}
}
