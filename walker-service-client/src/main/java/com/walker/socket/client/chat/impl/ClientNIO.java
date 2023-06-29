package com.walker.socket.client.chat.impl;

import com.walker.core.util.LangUtil;
import com.walker.socket.client.chat.frame.Client;
import com.walker.socket.frame.Session;
import com.walker.socket.util.SocketUtil;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientNIO<DATA> extends Client<SocketChannel, DATA> {

    public ClientNIO(String ip, int port, String name) throws Exception {
        super(ip, port, name);
    }

    @Override
    public void eventLoopKeeper() throws Exception {

        SocketChannel socket = SocketChannel.open();
        //设置为非阻塞模式
        socket.configureBlocking(false);

        //打开选择器asd
        Selector selector = Selector.open();

        //异步连接服务端？ 发送sync
        boolean connected = socket.connect(new InetSocketAddress(ip, port));
        if(connected){
            // 连接成功 直接注册读事件到selector中
            socket.register(selector, SelectionKey.OP_READ/*, ioHandler*/);
        }else{
            // 连接失败 客户端已发送sync 服务端没返回 ack 注册连接事件
            socket.register(selector, SelectionKey.OP_CONNECT/*, ioHandler*/);
        }

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
                if (key.isConnectable()) {  //上文注册的可连接事件
                    //获取事件相关的 channel 跟上面注册的是同一个对象
                    SocketChannel client = (SocketChannel) key.channel();
                    if(client.finishConnect()){ //已经连接ok ack 标记完成事件连接 否则会一直产生该事件?
                        client.register(selector, SelectionKey.OP_READ/*, ioHandler*/);
                        onNewConnection(makeUserSession(client));
                    }else{
                        onDisConnection();
                        break;
                    }
                } else if (key.isReadable()) {   // 读就绪
                    //获取 client‘s channel 跟上面注册绑定的是同一个对象
                    SocketChannel client = (SocketChannel) key.channel();
                    Session<SocketChannel, DATA> session = makeUserSession(client);
                    try {
                        DATA data = readSession(session);
                        if (data != null) {
                            onReceive(session, data);
                        }
                        //？？
//                        key.interestOps(SelectionKey.OP_READ);
                    } catch (InterruptedException ie) {
                        //中断信号只能被消费一次 ? 中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果不需要，则不用调用
//                Thread.currentThread().interrupt();
                        break;
                    }  catch (Exception e) {
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

