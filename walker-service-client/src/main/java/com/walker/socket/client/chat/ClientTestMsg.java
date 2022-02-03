package com.walker.socket.client.chat;


import com.walker.mode.Msg;
import com.walker.socket.base.encode.DataEncodeDecodeJson;
import com.walker.socket.client.chat.frame.ClientUI;
import com.walker.socket.client.chat.impl.*;

public class ClientTestMsg {

    public static void main(String[] args) throws Exception {
        int p = 9070;

//        {
//            ClientIO<Msg> client = new ClientIO<>("127.0.0.1", p++, "test");
//            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
//            new ClientUI(client, client.getClass().getSimpleName());
//
//        }
//        {
//            ClientNIO<Msg> client = new ClientNIO<>("127.0.0.1", p++, "test");
//            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
//            new ClientUI(client, client.getClass().getSimpleName());
//        }
//        {
//            ClientAIO<Msg> client = new ClientAIO<>("127.0.0.1", p++, "test");
//            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
//            new ClientUI(client, client.getClass().getSimpleName());
//
//        }
        {
            ClientNetty<Msg> client = new ClientNetty<>("127.0.0.1", p++, "test");
            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
            new ClientUI(client, client.getClass().getSimpleName());
        }
    }

}
