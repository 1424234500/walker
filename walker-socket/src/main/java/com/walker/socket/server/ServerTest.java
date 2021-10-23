package com.walker.socket.server;


import com.walker.socket.base.Msg;
import com.walker.socket.base.encode.DataEncodeDecodeJson;
import com.walker.socket.base.handler.HandlerEcho;
import com.walker.socket.base.handler.HandlerLog;
import com.walker.socket.base.handler.HandlerWrite;
import com.walker.socket.server.impl.ServerSocketIO;
import com.walker.socket.server.impl.ServerSocketNIO;
import com.walker.socket.server.impl.ServerSocketNetty;

import java.util.Arrays;

public class ServerTest {

    public static void main(String[] args) throws Exception {
        int p = 9070;

        new ServerSocketIO<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
                .start();
        new ServerSocketNIO<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
                .start();
        new ServerSocketNetty<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
                .start();

    }

}
