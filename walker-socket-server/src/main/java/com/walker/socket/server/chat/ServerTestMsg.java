package com.walker.socket.server.chat;


import com.walker.mode.Msg;
import com.walker.socket.base.encode.DataEncodeDecodeJson;
import com.walker.socket.base.handler.HandlerLog;
import com.walker.socket.base.handler.HandlerWrite;
import com.walker.socket.server.chat.impl.ServerSocketIO;
import com.walker.socket.server.chat.impl.ServerSocketNIO;
import com.walker.socket.server.chat.impl.ServerSocketNetty;
import com.walker.socket.server.chat.handler.HandlerSessionArpListImpl;

import java.util.Arrays;

public class ServerTestMsg {

    public static void main(String[] args) throws Exception {
        int p = 9070;


        new ServerSocketIO<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(
                        new HandlerLog<>()
                        , HandlerSessionArpListImpl.getInstance()
                        , new HandlerWrite<>()))
                .start();

        new ServerSocketNIO<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(
                        new HandlerLog<>()
                        , HandlerSessionArpListImpl.getInstance()
                        , new HandlerWrite<>()))
                .start();

        new ServerSocketNetty<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(
                        new HandlerLog<>()
                        , HandlerSessionArpListImpl.getInstance()
                        , new HandlerWrite<>()))
                .start();


    }

}
