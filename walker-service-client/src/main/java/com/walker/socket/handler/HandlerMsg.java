package com.walker.socket.handler;

import com.walker.socket.model.Msg;
import com.walker.socket.frame.Session;

public class HandlerMsg<SOCKET> extends HandlerAdapter<Session<SOCKET, Msg>, Msg> {


    @Override
    public void onReceive(Session<SOCKET, Msg> session, Msg data) throws Exception {
        data.setRes("hello world");
        sendServer(session, data);
    }
}
