package com.walker.socket.base.handler;


/**
 * 抽象socket接口要素
 */
public interface Handler<SESSION, DATA> {
    /**
     * socket 写入 发送
     * 收到一个 服务端服务请求  转发给对应对象的 客户端
     * 把msg信息系统 用户转发出去
     */
    void send(SESSION session, DATA data) throws Exception;

    /**
     * socket 接收 到消息 回调 key客户发了msg消息过来
     */
    void onReceive(SESSION session, DATA data) throws Exception;

    /**
     * socket 新连接
     */
    void onNewConnection(SESSION session);


    /**
     * socket 断开了连接
     */
    void onDisConnection(SESSION session);


} 