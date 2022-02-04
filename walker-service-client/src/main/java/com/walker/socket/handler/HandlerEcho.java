package com.walker.socket.handler;

public class HandlerEcho<SESSION, DATA> extends HandlerAdapter<SESSION, DATA> {


    @Override
    public void onReceive(SESSION session, DATA data) throws Exception {
        sendServer(session, data);
    }
}
