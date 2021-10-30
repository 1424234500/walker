package com.walker.socket.server.impl;

import com.walker.socket.base.Session;
import com.walker.socket.base.frame.ServerSessionEncode;
import com.walker.socket.base.util.SocketUtil;
import com.walker.util.LangUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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
        //打开channel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        //不设置port 会自动绑定随机port
        //The maximum number of pending connections
        serverSocket.bind(new InetSocketAddress(port), 1024);
        //设置为非阻塞模式
        serverSocket.configureBlocking(false);

        //打开选择器
        Selector selector = Selector.open();

        //注册等待事件
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        log.info(this + " bind成功 等待连接 端口监听");
        while (!Thread.currentThread().isInterrupted()) {
            //选择器 扫描（信号触发） 获取事件清单
            int wait = selector.select(10000);
            log.info("当前等待处理的事件 " + wait + " 个");
            if (wait <= 0) {
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
                //处理事件 考虑分发worker线程池处理
                log.info(LangUtil.turnObj2Map(key, "is").toString());
                if(!key.isValid()) continue;
                if (key.isAcceptable()) {   // 第一次只注册了 serverSocketChannel OP_ACCEPT 事件产生时
                    //获取事件相关的 channel 跟上面注册的是同一个对象
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //拿到 server 获取链接的 client‘s channel
                    SocketChannel socket = server.accept();
                    //非阻塞模式
                    socket.configureBlocking(false);
                    //client‘s channel 注册到selector上 之后这个client‘s channel的 OP_READ 事件都交给 selector负责检查事件驱动
                    socket.register(selector, SelectionKey.OP_READ);
                    onNewConnection(makeUserSession(socket));

                    // ？？？ 将此对应的channel设置为准备接受其他客户端请求
//                    key.interestOps(SelectionKey.OP_ACCEPT);
                }else if (key.isReadable()) {   // 读就绪
                    //获取 client‘s channel 跟上面注册绑定的是同一个对象
                    SocketChannel socket = (SocketChannel) key.channel();
                    Session<SocketChannel, DATA> session = makeUserSession(socket);
                    try {
                        DATA data = readSession(session);
                        if (data != null) {
                            onReceive(session, data);
                        }
                        //？？
//                        key.interestOps(SelectionKey.OP_READ);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
//                        key.cancel();
//                        if (key.channel() != null) {
//                            key.channel().close();
//                        }
                    }
                }
            }

        }
//      中断后进入这里 意味着关闭server
        if (selector != null) {
            // selector关闭后 所有注册上面的channel pipe等资源都会自动关闭！
            selector.close();
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
