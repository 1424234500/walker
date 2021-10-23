package com.walker.socket.client;


import com.walker.socket.base.Msg;
import com.walker.socket.base.encode.DataEncodeDecodeJson;
import com.walker.socket.client.impl.ClientIO;
import com.walker.socket.client.impl.ClientNIO;
import com.walker.socket.client.impl.ClientNetty;

public class ClientTestMsg {

    public static void main(String[] args) throws Exception {
        int p = 9070;

        {
            ClientIO<Msg> client = new ClientIO<>("127.0.0.1", p++, "test");
            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
            new ClientUI(client, client.getClass().getSimpleName());

        }
        {
            ClientNIO<Msg> client = new ClientNIO<>("127.0.0.1", p++, "test");
            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
            new ClientUI(client, client.getClass().getSimpleName());

        }
        {
            ClientNetty<Msg> client = new ClientNetty<>("127.0.0.1", p++, "test");
            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
            new ClientUI(client, client.getClass().getSimpleName());
        }
//
//        {
//            ClientStartStopTest<Msg> client = new ClientStartStopTest<>("127.0.0.1", p++, "test");
//            client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
//            new ClientUI(client, client.getClass().getSimpleName());
//        }
    }

}
