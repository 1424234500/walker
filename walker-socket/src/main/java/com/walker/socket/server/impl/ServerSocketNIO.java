package com.walker.socket.server.impl;

import com.walker.socket.base.Session;
import com.walker.socket.base.impl.ServerSessionEncode;
import com.walker.socket.util.SocketUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 初始化一个socketServer
 * 绑定一个handler
 * 即可在handler处理各种连接和事件
 */


public class ServerSocketNIO<DATA> extends ServerSessionEncode<SocketChannel, DATA> {

    public ServerSocketNIO(int port) throws Exception {
        super(port);
    }

    @Override
    public String makeSocketKey(SocketChannel socketChannel) {
        return SocketUtil.makeKey(socketChannel);
    }

    @Override
    public void eventLoopKeeper() throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);  //注册等待事件

        log.info(this + " bind成功 等待连接 端口监听");
        while (!Thread.currentThread().isInterrupted()) {
            //1.在轮询获取待处理的事件
            int wait = selector.select();
            log.info("当前等待处理的事件 " + wait + " 个");
            if (wait == 0) {
                continue;
            }
            //获取所有待处理的事件
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            //遍历
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //处理前，关闭选在择器中的事件
                iterator.remove();
                //处理事件 是否需要多线程处理
                log.info("event: " + key.toString() + " Readable: " + key.isReadable() + " isAcceptable: " + key.isAcceptable());
                if (key.isAcceptable()) { //连接就绪
                    //获取通道
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //进入服务端等待
                    SocketChannel socket = server.accept();
                    //非阻塞模式
                    socket.configureBlocking(false);
                    //注册选择器，并设置为读取模式，收到一个连接请求， 然后起一个SocketChannel，并注册到selector上，
                    // 之后这个连接的数据，就由这个SocketChannel处理
                    socket.register(selector, SelectionKey.OP_READ);
                    //将此对应的channel设置为准备接受其他客户端请求
                    key.interestOps(SelectionKey.OP_ACCEPT);
                    onNewConnection(makeUserSession(socket));
                }
                if (key.isReadable()) {//读就绪
                    //返回该SelectionKey对应的 Channel，其中有数据需要读取
                    SocketChannel socket = (SocketChannel) key.channel();
                    Session<SocketChannel, DATA> session = makeUserSession(socket);
                    try {
                        DATA data = read(session);
                        if (data != null) {
                            onReceive(session, data);
                        }
                        key.interestOps(SelectionKey.OP_READ);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        key.cancel();
                        if (key.channel() != null) {
                            key.channel().close();
                        }
                    }
                }
            }

        }
//      中断后进入这里 意味着关闭server
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    @Override
    public String readString(SocketChannel socket) throws Exception {
        return SocketUtil.readImpl(socket);
    }

    @Override
    public void writeString(SocketChannel socket, String data) throws Exception {
        SocketUtil.sendImpl(socket, data);
    }

}
