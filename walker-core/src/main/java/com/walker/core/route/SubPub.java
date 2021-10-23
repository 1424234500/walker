package com.walker.core.route;

import com.walker.core.route.SubPub.OnSubscribe.Type;

import java.util.List;

/**
 * 阻塞
 * 
 * 订阅发布模型
 * 
 * 跨线程map实现
 * 跨进程redis实现
 *
 */
public interface SubPub<T, V> {
	void init(Integer threadSize);
	
	/**
	 * 发布者 承担 负责调用订阅执行
	 * @param channel
	 * @param object
	 * @return 返回订阅者处理的返回的Key集合
	 */
	List<V> publish(String channel, T object);
	
	/**
	 * 订阅 提供回调供发布者调用
	 * @param channel
	 * @param onSubscribe
	 * @return 订阅序数
	 */
	Integer subscribe(String channel, OnSubscribe<T, V> onSubscribe);
	/**
	 * 取消订阅 
	 * @param channel
	 * @param onSubscribe
	 * @return 订阅序数
	 */
	Integer unSubscribe(String channel, OnSubscribe<T, V> onSubscribe);
	
	/**
	 *	结果 和 是否中断其他订阅
	 * @param <V>
	 */
	class Res<V>{
		public Res(Type type, V res) {
			this.res = res;
			this.type = type;
		}
		V res;
		Type type;
	}
	/**
	 * 订阅处理回调
	 * 
	 * @param <T>
	 */
	interface OnSubscribe<T, V>{
		/**
		 * 订阅事件注册
		 * @param object
		 * @return	返回接收到的用户的key 上传  需保证同用户在同服务器 避免服务器之间的上传
		 */
		 Res<V> onSubscribe(T object);
		
		enum Type{
			/**
			 * 已处理过 是否停止其他订阅
			 */
			STOP, 
			/**
			 * 未处理
			 */
			DEFAULT,
			/**
			 * 已处理过 是否继续处理
			 */
			DONE,
		}
	}

}






