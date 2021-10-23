package com.walker.socket.client.impl;

import com.walker.socket.util.SocketUtil;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientNIO<DATA> extends Client<SocketChannel, DATA> {

    public ClientNIO(String ip, int port, String name) throws Exception {
        super(ip, port, name);
    }

    @Override
    public void eventLoopKeeper() throws Exception {

        SocketChannel socket = SocketChannel.open();
        socket.connect(new InetSocketAddress(ip, port));

        onNewConnection(makeUserSession(socket));

        while (!Thread.currentThread().isInterrupted()) {
            try {
                DATA data = read();
                if (data != null) {
                    onReceive(data);
                }
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                //中断信号只能被消费一次 ? 中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果不需要，则不用调用
//                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
//      中断后进入这里 意味着关闭server
    }

    @Override
    public String readString(SocketChannel socket) throws Exception {
        return SocketUtil.readImpl(socket);
    }

    @Override
    public void writeString(SocketChannel socket, String data) throws Exception {
        SocketUtil.sendImpl(socket, data);
    }

    @Override
    public String makeSocketKey(SocketChannel socket) {
        return SocketUtil.makeKey(socket);
    }
}

