package com.walker.socket.base.handler;

import com.walker.mode.Msg;
import com.walker.socket.base.Session;

public class HandlerMsg<SOCKET> extends HandlerAdapter<Session<SOCKET, Msg>, Msg> {


    @Override
    public void onReceive(Session<SOCKET, Msg> session, Msg data) throws Exception {
        data.setRes("hello world");
        sendServer(session, data);
    }
}
