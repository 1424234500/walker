package com.walker.mode;

import com.walker.core.encode.JsonUtil;
import com.walker.util.Bean;

import java.io.Serializable;
import java.util.*;

/**
 * socket 传递 消息结构 
 * 
 * who 发来了一条消息  session:socket:key
 * {type:login,data:{user:walker,pwd:1234,device:xxxxx} }	
 * 				{type:login,status:0,data:{user:walker,pwd:1234,device:xxxxx} }	
 * 				{type:login,status:1,info:重复id,data:{user:walker,pwd:1234,device:xxxxx} }	
 * 
 * {type:message,data:{user:walker,pwd:1234} }		
 * 
 * 客户端写入数据
 * 	type:login	登录请求
 * 	data:{}		登录请求的参数
 * 
 * 	time_client	客户端发送时间
 * 	
 * 		
 */
public class Msg extends Bean implements Cloneable, Serializable {
	public static final long serialVersionUID = 1L;
	final public static String SPLIT = ",";
	
	//系统上下文 函数调用控制

	//记录关键时间节点 统计计算
	//time_client - 网络传输耗时 - time_receive - 队列等待耗时 - time_do - 业务处理耗时 - time_send
	final public static String KEY_TIME_CLIENT = "TC";	//client send time
	final public static String KEY_TIME_RECEIVE = "TR";	//server receive time to pipe
	final public static String KEY_TIME_DO = "TD";			//server dubbo time
	final public static String KEY_TIME_SEND = "TS";		//server send time
	
	final public static String KEY_WAIT_SIZE = "WS";		//pipe 队列等待深度
	final public static String KEY_INFO = "INFO";					//about

	
	//记录socket收发ip port key
	final public static String KEY_FROM = "SF";	//socket from
	final public static String KEY_TO = "ST";		//socket to
	//记录业务 类型 参数
	final public static String KEY_TYPE = "TYPE";	//plugin type
	final public static String KEY_USER_FROM = "FROM";	//user from	user a -> user b
	final public static String KEY_USER_TO = "TO";		//user to	user a -> group
	
	final public static String KEY_STATUS = "STATUS";	//msg res status
	final public static String KEY_DATA = "DATA";	//msg data bean

	
	
	
	
	public Msg() {}
	public Msg(Map<?,?> bean) {
		super(bean);
	}
	public Msg(String json) {
		int t = JsonUtil.getType(json);
		if(t == 1){
			Bean bean = (Bean)JsonUtil.get(json);
			this.putAll(bean);	//是否过滤非必须字段 
//			this.setType(bean.get(KEY_TYPE, ""));//确保type
//			this.setUserTo(bean.get(KEY_USER_TO, ""));
//			this.setUserFrom(bean.get(KEY_USER_FROM, ""));
//			this.setData(bean.get(KEY_DATA));
			
//			if(this.getTimeClient() == 0) {//避免不传导致 计算异常
				this.setTimeClient(System.currentTimeMillis());//确保type
//			}


		}else {
			this.setType("echo");
			this.setData(new Bean().set("json", json));
		}
	}

	
	
	public Msg setFrom(String from) {
		this.set(KEY_FROM, from);
		return this;
	}
	public Msg setTo(String to) {
		this.set(KEY_TO, to);
		return this;
	}
	public Msg setType(String type) {
		this.set(KEY_TYPE, type);
		return this;
	}
	public Msg setData(Object data) {
		this.set(KEY_DATA, data);
		return this;
	}
	
	public String getFrom() {
		return this.get(KEY_FROM, "");
	}
	public String getTo() {
		return this.get(KEY_TO, "");
	}
	public String getType() {
		return this.get(KEY_TYPE, "");
	}
	public <T> T getData() {
		return (T)this.get(KEY_DATA);
	}
	public Msg setStatus(int status) {
		this.set(KEY_STATUS, status);
		return this;
	}
	public int getStatus() {
		return this.get(KEY_STATUS, -1);
	}
	/**
	 * 来源 User结构体
	 * @param from
	 */
	public Msg setUserFrom(Bean from) {
		this.set(KEY_USER_FROM, from);
		return this;
	}
	/**
	 * 去向 id1,id2,id3 字符串 订阅处自行设置to实体User
	 * @param to
	 */
	public Msg setUserTo(String to) {
		List<String> list = Arrays.asList(to.split(SPLIT));
		Set<String> set = new HashSet<>(list);
		String res = String.join(",", set);
		this.set(KEY_USER_TO, res);
		return this;
	}
	public Msg addUserTo(String to) {
		Set<String> list = new HashSet<String>();
		String tos[] = this.getUserTo();
		for(String str : tos) {
			list.add(str);
		}
		String tos1[] = to.split(SPLIT);
		for(String str : tos1) {
			list.add(str);
		}
		String res = String.join(",", list);
		this.set(KEY_USER_TO, res);
		return this;
	}
	public Msg removeUserTo(String...tos) {
		List<String> list = Arrays.asList(this.get(KEY_USER_TO, "").split(SPLIT));
		List<String> add = Arrays.asList(tos);
		list.removeAll(add);
		Set<String> set =new HashSet<>(list);
		String res =String.join(",", set);
		this.set(KEY_USER_TO, res);
		return this;
	}
	public String getUserToStr(){
		return this.get(KEY_USER_TO, "");
	}
	public String[] getUserTo() {
		return this.get(KEY_USER_TO, "").split(SPLIT);
	}
	public UserSocket getUserFrom() {
		return new UserSocket(this.get(KEY_USER_FROM, new Bean()));
	}
	

	public Msg setTimeClient(long timeMill) {
		this.set(KEY_TIME_CLIENT, timeMill);
		return this;
	}
	public Long getTimeClient() {
		return this.get(KEY_TIME_CLIENT, 0L);
	}
	public Msg setTimeReceive(long timeMill) {
		this.set(KEY_TIME_RECEIVE, timeMill);
		return this;
	}
	public Long getTimeReceive() {
		return this.get(KEY_TIME_RECEIVE, 0L);
	}
	public Msg setTimeDo(long timeMill) {
		this.set(KEY_TIME_DO, timeMill);
		return this;
	}
	public Long getTimeDo() {
		return this.get(KEY_TIME_DO, 0L);
	}
	public Msg setTimeSend(long timeMill) {
		this.set(KEY_TIME_SEND, timeMill);
		return this;
	}
	public Long getTimeSend() {
		return this.get(KEY_TIME_SEND, 0L);
	}
	
	public Msg setWaitSize(Long size) {
		this.set(KEY_WAIT_SIZE, size);
		return this;
	}
	public Long getWaitSize() {
		return this.get(KEY_WAIT_SIZE, 0L);
	}
	public Msg setInfo(Object info) {
		this.set(KEY_INFO, info);
		return this;
	}
	public Object getInfo() {
		return this.get(KEY_INFO);
	}
}
