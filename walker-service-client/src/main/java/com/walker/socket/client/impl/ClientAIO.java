package com.walker.socket.client.impl;

import com.walker.core.aop.FunArgs;
import com.walker.socket.base.Session;
import com.walker.socket.base.SocketReadOkError;
import com.walker.socket.base.util.SocketUtil;
import com.walker.socket.client.frame.Client;
import com.walker.util.LangUtil;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ClientAIO<DATA> extends Client<AsynchronousSocketChannel, DATA> {

    public ClientAIO(String ip, int port, String name) throws Exception {
        super(ip, port, name);
    }

    @Override
    public void eventLoopKeeper() throws Exception {
        AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();

        socket.connect(new InetSocketAddress(ip, port), socket, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @SneakyThrows
            @Override
            public void completed(Void result, AsynchronousSocketChannel socket) {

                Session<AsynchronousSocketChannel, DATA> session = makeUserSession(socket);
                onNewConnection(makeUserSession(socket));

                // 循环问题 用完一次 callback 再重新注册一次
                SocketUtil.readImpl(socket, new SocketReadOkError() {
                    @Override
                    public void readSuccess(String line) {
                        if(line != null && line.length() > 0){
                            try {
                                DATA data = decode(line);
                                if (data != null) {
                                    onReceive(session, data);
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }

                    @Override
                    public boolean readException(Throwable e) {
                        return true;
                    }
                });
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                log.error("accept failed " + exc.getMessage(), exc);
            }
        });
        while (!Thread.currentThread().isInterrupted()) {
            Thread.sleep(50);
        }
//      中断后进入这里 意味着关闭server
        if(socket != null){
            socket.close();
        }
    }

    @Override
    public String readString(AsynchronousSocketChannel socket) throws Exception {
        throw new RuntimeException("aio no this way");
    }

    @Override
    public void writeString(AsynchronousSocketChannel socket, String data) throws Exception {
        SocketUtil.sendImpl(socket, data);
    }

    @Override
    public String makeSocketKey(AsynchronousSocketChannel socket) {
        return SocketUtil.makeKey(socket);
    }
}

