package com.walker.socket.base.handler;

public class HandlerWrite<SESSION, DATA> extends HandlerAdapter<SESSION, DATA> {

    /**
     * socket 写入 发送
     * 收到一个 服务端服务请求  转发给对应对象的 客户端
     * 把msg信息系统 用户转发出去
     */
    public void send(SESSION session, DATA data) throws Exception {
        writeServer(session, data);
    }

}
