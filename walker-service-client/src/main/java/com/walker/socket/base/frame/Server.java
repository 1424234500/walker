package com.walker.socket.base.frame;

import com.walker.socket.base.StartStop;
import com.walker.socket.base.StartStopAdapter;
import com.walker.socket.base.handler.Handler;
import com.walker.socket.base.handler.HandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化一个socketServer
 * 绑定一个handler
 * 即可在handler处理各种连接和事件
 * <p>
 * 壳子 处理链 编码解码器
 * <p>
 * 具体处理依赖底层子类实现
 */
public abstract class Server<SESSION, DATA> extends StartStopAdapter implements Handler<SESSION, DATA>, StartStop {
    private static final Charset charset = StandardCharsets.UTF_8;
    public Logger log = LoggerFactory.getLogger(getClass());
    public String ip;
    public String name;
    public int port;
    /**
     * 业务处理链式节点
     */
    List<HandlerAdapter<SESSION, DATA>> handlers = new ArrayList<>();


    public Server(String ip, int port, String name) {
        this.ip = ip;
        this.name = name;
        this.port = port;
    }

    /**
     * 读取流 实现方式
     */
    public abstract DATA readSession(SESSION session) throws Exception;

    /**
     * 写入流 实现方式
     */
    public abstract void write(SESSION session, DATA data) throws Exception;

    @Override
    public String toString() {
        return getClass() + "{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", port=" + port +
                '}';
    }

    public Server<SESSION, DATA> setHandlerChain(List<HandlerAdapter<SESSION, DATA>> handlers) {
        handlers.forEach(item -> item.setServer(this));
        this.handlers = handlers;
        return this;
    }

    public List<HandlerAdapter<SESSION, DATA>> getHandlerChainHead() {
        return handlers;
    }


    @Override
    public void send(SESSION session, DATA str) throws Exception {
        for (HandlerAdapter<SESSION, DATA> handler : handlers) {
            handler.send(session, str);
        }
    }

    @Override
    public void onReceive(SESSION session, DATA str) throws Exception {
        for (HandlerAdapter<SESSION, DATA> handler : handlers) {
            handler.onReceive(session, str);
        }
    }

    @Override
    public void onNewConnection(SESSION session) {
        for (HandlerAdapter<SESSION, DATA> handler : handlers) {
            handler.onNewConnection(session);
        }
    }

    @Override
    public void onDisConnection(SESSION session) {
        for (HandlerAdapter<SESSION, DATA> handler : handlers) {
            handler.onDisConnection(session);
        }
    }
}
