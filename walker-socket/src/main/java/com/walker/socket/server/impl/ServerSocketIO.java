package com.walker.socket.server.impl;

import com.walker.socket.base.Session;
import com.walker.socket.base.impl.ServerSessionEncode;
import com.walker.socket.util.SocketUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 初始化一个socketServer
 * 绑定一个handler
 * 即可在handler处理各种连接和事件
 */

public class ServerSocketIO<DATA> extends ServerSessionEncode<Socket, DATA> {

    int threadSize = 10;
    int ecMax = 10;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(this.threadSize, this.threadSize
            , 0L, TimeUnit.MILLISECONDS
            , new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE)
            , new ThreadPoolExecutor.CallerRunsPolicy());

    public ServerSocketIO(int port) throws Exception {
        super(port);
    }

    @Override
    public String makeSocketKey(Socket socket) {
        return SocketUtil.makeKey(socket);
    }

    @Override
    public void eventLoopKeeper() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        log.info(ServerSocketIO.this + " bind成功 等待连接 端口监听");
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = serverSocket.accept();
            Session<Socket, DATA> session = makeUserSession(socket);
            super.onNewConnection(session);
            //单线程单socket专用读写
            pool.execute(new Runnable() {
                int ec = ecMax;

                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && ec > 0) {
                        try {
                            DATA data = read(session);
                            if (data != null) {
                                onReceive(session, data);
                            }
                            Thread.sleep(50);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            ec--;
                        }
                    }
                    onDisConnection(session);
                    try {
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }
//      中断后进入这里 意味着关闭server
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    @Override
    public String readString(Socket socket) throws Exception {
        return SocketUtil.readImpl(socket);
    }

    @Override
    public void writeString(Socket socket, String data) throws Exception {
        SocketUtil.sendImpl(socket, data);
    }

}
