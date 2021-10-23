package com.walker.socket.base.handler;

public class HandlerLog<SESSION, DATA> extends HandlerAdapter<SESSION, DATA> {

    /**
     * socket 写入 发送
     * 收到一个 服务端服务请求  转发给对应对象的 客户端
     * 把msg信息系统 用户转发出去
     */
    public void send(SESSION session, DATA data) throws Exception {
        log.info("send >> " + data + " to " + session);
    }

    /**
     * socket 接收 到消息 回调 key客户发了msg消息过来
     */

    @Override
    public void onReceive(SESSION session, DATA data) throws Exception {
        log.info("recv << " + data + " from " + session);
    }

    /**
     * socket 新连接
     */
    public void onNewConnection(SESSION session) {
        log.info("newc == " + session);
    }


    /**
     * socket 断开了连接
     */
    public void onDisConnection(SESSION session) {
        log.info("disc != " + session);
    }

}
