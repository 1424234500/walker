package com.walker.socket.base.impl;

import com.walker.socket.base.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 转换对象为userSession
 * 适配默认配置
 */
public abstract class ServerSession<SOCKET, DATA> extends Server<Session<SOCKET, DATA>, DATA> {
    private static String getIp;
    private static String getName;

    static {
        try {
            getIp = InetAddress.getLocalHost().getHostAddress();
            getName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public ServerSession(int port) {
        super(getIp, port, getName);
    }

    public ServerSession(String ip, int port, String name) {
        super(ip, port, name);
    }

    //    socket转session
    public Session<SOCKET, DATA> makeUserSession(SOCKET socket) {
        return new Session<SOCKET, DATA>(socket) {
            @Override
            public String key() {
                return makeSocketKey(this.socket);
            }

            @Override
            public void send(DATA data) throws Exception {
//                session订阅时自己发送 不依赖server 解耦?
                write(this, data);
            }
        };
    }

    public abstract String makeSocketKey(SOCKET socket);

}