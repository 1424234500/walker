package com.walker.socket.server.chat.impl;

import com.walker.socket.frame.Session;
import com.walker.socket.frame.SocketReadOkError;
import com.walker.socket.frame.ServerSessionEncode;
import com.walker.socket.util.SocketUtil;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;

/**
 * IO 多路复用模型 ? NIO 1.0 ?
 *
 * linux
 * 提供select/poll select 轮询扫描 fd s
 * 提供 epoll 伪AIO callback 事件驱动代替顺序扫描
 *
 * 初始化一个socketServer
 * 绑定一个handler
 * 即可在handler处理各种连接和事件
 */


public class ServerSocketAIO<DATA> extends ServerSessionEncode<AsynchronousSocketChannel, DATA> {

    public ServerSocketAIO(int port) throws Exception {
        super(port);
    }

    @Override
    public String makeSocketKey(AsynchronousSocketChannel socketChannel) {
        return SocketUtil.makeKey(socketChannel);
    }

    @Override
    public void eventLoopKeeper() throws IOException {
        //打开channel
        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open();
        //不设置port 会自动绑定随机port
        //The maximum number of pending connections
        serverSocket.bind(new InetSocketAddress(port), 1024);

        log.info(this + " bind成功 等待连接 端口监听");
        // 异步accept 等待连接后 自动回调处理 ATTACH 绑定传递参数  一次回调建立accept之后 需要循环等待下次accept回调
        serverSocket.accept(this, new CompletionHandler<AsynchronousSocketChannel, ServerSocketAIO<DATA>>() {
            @SneakyThrows
            @Override
            public void completed(AsynchronousSocketChannel socket, ServerSocketAIO<DATA> attachment) {
                // 循环问题 用完一次 callback 再重新注册一次
                serverSocket.accept(attachment, this);

                Session<AsynchronousSocketChannel, DATA> session = makeUserSession(socket);
                onNewConnection(makeUserSession(socket));

                // 循环问题 用完一次 callback 再重新注册一次
                SocketUtil.readImpl(socket, new SocketReadOkError() {
                    @Override
                    public void readSuccess(String line) {
                        if (line != null && line.length() > 0) {
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
            public void failed(Throwable exc, ServerSocketAIO<DATA> attachment) {
                log.error("accept failed " + exc.getMessage(), exc);
            }
        });
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
//      中断后进入这里 意味着关闭server
        if (serverSocket != null) {
            // selector关闭后 所有注册上面的channel pipe等资源都会自动关闭！
            serverSocket.close();
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

}
