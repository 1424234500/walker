package com.walker.socket.server.chat;


import com.walker.core.Context;
import com.walker.socket.encode.DataEncodeDecodeJson;
import com.walker.socket.handler.HandlerEcho;
import com.walker.socket.handler.HandlerLog;
import com.walker.socket.handler.HandlerWrite;
import com.walker.socket.model.Msg;
import com.walker.socket.server.chat.impl.ServerSocketNetty;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;

public class ServerTest {

    public static void main(String[] args) throws Exception {
        int p = 9070;
        System.setProperty("path_conf", "conf");
        PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

//        new ServerSocketIO<Msg>(p++)
//                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
//                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
//                .start();
//        new ServerSocketNIO<Msg>(p++)
//                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
//                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
//                .start();
//        new ServerSocketAIO<Msg>(p++)
//                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
//                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
//                .start();
        new ServerSocketNetty<Msg>(p++)
                .setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
                .setHandlerChain(Arrays.asList(new HandlerLog<>(), new HandlerEcho<>(), new HandlerWrite<>()))
                .start();


    }

}
