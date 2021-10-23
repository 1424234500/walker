package com.walker.socket.base.handler;

import com.walker.socket.base.impl.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HandlerAdapter<SESSION, DATA> implements Handler<SESSION, DATA> {
    public Logger log = LoggerFactory.getLogger(getClass());


    private Server<SESSION, DATA> server;

    public Handler<SESSION, DATA> setServer(Server<SESSION, DATA> server) {
        this.server = server;
        return this;
    }

    public void sendServer(SESSION session, DATA data) throws Exception {
        server.send(session, data);
    }

    public void writeServer(SESSION session, DATA data) throws Exception {
        server.write(session, data);
    }


    /**
     * socket 写入 发送
     * 收到一个 服务端服务请求  转发给对应对象的 客户端
     * 把msg信息系统 用户转发出去
     */
    public void send(SESSION session, DATA data) throws Exception {
//        server.write(session, data);
    }

    /**
     * socket 接收 到消息 回调 key客户发了msg消息过来
     */

    @Override
    public void onReceive(SESSION session, DATA data) throws Exception {
    }

    /**
     * socket 新连接
     */
    public void onNewConnection(SESSION session) {
    }


    /**
     * socket 断开了连接
     */
    public void onDisConnection(SESSION session) {

    }

}
