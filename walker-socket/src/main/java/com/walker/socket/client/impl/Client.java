package com.walker.socket.client.impl;

import com.walker.socket.base.Session;
import com.walker.socket.base.handler.HandlerAdapter;
import com.walker.socket.base.handler.HandlerLog;
import com.walker.socket.base.handler.HandlerWrite;

import java.util.Arrays;

public abstract class Client<SOCKET, DATA> extends ServerSessionEncodeClient<SOCKET, DATA> {

    HandlerAdapter<Session<SOCKET, DATA>, DATA> handlerUi = new HandlerAdapter<Session<SOCKET, DATA>, DATA>() {

        /**
         * socket 写入 发送
         * 收到一个 服务端服务请求  转发给对应对象的 客户端
         * 把msg信息系统 用户转发出去
         */
        public void send(Session<SOCKET, DATA> session, DATA data) throws Exception {
            getOut().out("send >> " + data + " to " + session);

        }

        /**
         * socket 接收 到消息 回调 key客户发了msg消息过来
         */

        @Override
        public void onReceive(Session<SOCKET, DATA> session, DATA data) throws Exception {
            getOut().out("recv << " + data + " from " + session);
        }

        /**
         * socket 新连接
         */
        public void onNewConnection(Session<SOCKET, DATA> session) {
            getOut().out("newc == " + session);
        }


        /**
         * socket 断开了连接
         */
        public void onDisConnection(Session<SOCKET, DATA> session) {
            getOut().out("disc != " + session);
        }
    };

    public Client(String ip, int port, String name) throws Exception {
        super(ip, port, name);
//        每个client自己实现处理器链
        this.setHandlerChain(Arrays.asList(
                new HandlerLog<>()
                , handlerUi
                , new HandlerWrite<>()
        ));
    }


}
