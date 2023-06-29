package com.walker.socket.util;

import com.walker.core.util.LangUtil;
import com.walker.socket.frame.SocketReadOkError;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 包协议
 * 4个字节 长度 size
 * 正文\r
 *
 */
public class SocketUtil {
    public static final int BYTE_BUFFER_BATCH_SIZE = 1024;
    
    /**
     * socket io 阻塞模式读取
     */
    public static String readImpl(Socket socket) throws Exception {
        String res = "";
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);

        if (isr.ready()) {
            byte[] head = new byte[4];
            int read = is.read(head, 0, head.length);    //尝试读取数据流 的头4个字节<int> 读取长度 -1表示读取到了数据流的末尾了；
            if (read != -1) {
                int size = LangUtil.bytes2int(head);    //头4个字节 int 大小 int = 4byte = 32bit
                int readCount = 0;
                StringBuilder sb = new StringBuilder();
                while (readCount < size) {  //读取已知长度消息内容 异步读取 死循环 直到读够目标字节数
                    byte[] buffer = new byte[BYTE_BUFFER_BATCH_SIZE];
                    read = is.read(buffer, 0, Math.min(size - readCount, buffer.length));
                    if (read != -1) {
                        readCount += read;
                        sb.append(new String(buffer, StandardCharsets.UTF_8));
                    }
                }
                res = sb.toString();
            }
        }
        return res;
    }

    /**
     * socket io 阻塞模式发送
     */
    public static void sendImpl(Socket socket, String jsonstr) throws Exception {
        if (jsonstr == null || jsonstr.length() == 0) return;
        byte[] bytes = jsonstr.getBytes();
        OutputStream os = socket.getOutputStream();
        os.write(LangUtil.int2bytes(bytes.length));    //int = 4byte = 32bit
        os.write(bytes);
//		os.write('\r');
//		os.write('\n');
        os.flush();
    }


    /**
     * socket nio 非阻塞模式发送字节包
     */
    synchronized public static void sendImpl(SocketChannel socket, String jsonstr) throws Exception {
        if (jsonstr == null || jsonstr.length() == 0) return;

        byte[] bytes = jsonstr.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + size);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        // 异步写入 不保证一次性写入完成问题
        while (buffer.hasRemaining()) {
            socket.write(buffer);
        }
    }

    /**
     * socket nio 非阻塞模式读取字节包
     */
    public static String readImpl(SocketChannel socket) throws Exception {
        String res = "";

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.clear();
        int read = socket.read(byteBuffer);    //尝试读取数据流 的头4个字节<int> 读取长度 -1表示读取到了数据流的末尾了；
        if (read != -1) {    //若有数据<0.2条数据 1条数据 3.2条数据>
            byteBuffer.flip();
            int size = byteBuffer.getInt();    //头4个字节 int 大小 int = 4byte = 32bit

            int readCount = 0;
            byte[] b = new byte[BYTE_BUFFER_BATCH_SIZE];
            ByteBuffer buffer = ByteBuffer.allocate(BYTE_BUFFER_BATCH_SIZE);
            StringBuilder sb = new StringBuilder();
            while (readCount < size) {  //读取已知长度消息内容 异步读取 死循环 直到读够目标字节数
                buffer.clear();
                read = socket.read(buffer);
                if (read != -1) {
                    readCount += read;
                    buffer.flip();
                    int index = 0;
                    while (buffer.hasRemaining()) {
                        b[index++] = buffer.get();
                        if (index >= b.length) {
                            index = 0;
                            sb.append(new String(b, StandardCharsets.UTF_8));
                        }
                    }
                    if (index > 0) {
                        sb.append(new String(b, StandardCharsets.UTF_8));
                    }
                }
            }
            res = sb.toString();
        }
        return res;
    }


    /**
     * socket aio 非阻塞模式发送字节包
     */
    public static void sendImpl(AsynchronousSocketChannel socket, String jsonstr)  {
        if (jsonstr == null || jsonstr.length() == 0) return;

        byte[] bytes = jsonstr.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + size);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        socket.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if(buffer.hasRemaining()){
                    socket.write(buffer, buffer, this);
                }
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                buffer.clear();
            }
        });
    }


    /**
     * socket nio 非阻塞模式读取字节包
     */
    public static void readImpl(AsynchronousSocketChannel socket, SocketReadOkError socketReadOkError) throws Exception {
        try {
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            sizeBuffer.clear();
            // 异步读取 等待读取后 自动回调处理 ATTACH 绑定传递参数
            socket.read(sizeBuffer, sizeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @SneakyThrows
                @Override
                public void completed(Integer length, ByteBuffer sizeBuffer) {
                    try {
                        // 读取一定会读满 buffer 吗？？
                        sizeBuffer.flip();
                        byte[] bint = new byte[sizeBuffer.remaining()];
                        sizeBuffer.get(bint);
                        int size = LangUtil.bytes2int(bint);

                        ByteBuffer buffer = ByteBuffer.allocate(size);
                        buffer.clear();
                        socket.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                            @SneakyThrows
                            @Override
                            public void completed(Integer length, ByteBuffer buffer) {
                                readImpl(socket, socketReadOkError);

                                String line = new String(buffer.array(), StandardCharsets.UTF_8);

                                socketReadOkError.readSuccess(line);
                            }

                            @SneakyThrows
                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                if (socketReadOkError.readException(exc)) {
                                    // 循环问题 用完一次 callback 再重新注册一次
                                    readImpl(socket, socketReadOkError);
                                } else {
                                    throw new RuntimeException(exc);
                                }
                            }
                        });
                    } catch (Throwable e) {
                        System.out.println("read error1 " + e.getMessage());
                        e.printStackTrace();
                        readImpl(socket, socketReadOkError);
                    }

                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    socketReadOkError.readException(exc);
                }
            });
        } catch (Throwable e) {
            System.out.println("read error " + e.getMessage());
            e.printStackTrace();
            readImpl(socket, socketReadOkError);
        }
    }


    public static String makeKey(Socket socket) {
        return socket.toString().split("/")[1].split(",lo")[0].replaceAll(",port=", ":");
    }


    public static String makeKey(ChannelHandlerContext channelHandlerContext) {
        //ChannelHandlerContext(SessionHandler#0, [id: 0x9a9c3c84, L:/127.0.0.1:8092 - R:/127.0.0.1:34612])
        String[] ss = channelHandlerContext.toString().split(" ");
        //R:/127.0.0.1:45316])
        String key = ss[ss.length - 1];
        //127.0.0.1:34612
        key = key.substring(3).split("\\]")[0];
        return key;
    }

    public static String makeKey(SocketChannel socketChannel) {
        if(socketChannel == null || ! socketChannel.isConnected() || ! socketChannel.isOpen()){
            throw new RuntimeException("" + socketChannel + " is closed ");
        }
        return socketChannel.toString().split("=")[2].substring(1).replace(']', ' ').replaceAll(" ", "");
    }

    public static String makeKey(AsynchronousSocketChannel socketChannel) {
        if(socketChannel == null || ! socketChannel.isOpen()){
            throw new RuntimeException("" + socketChannel + " is closed ");
        }
        return socketChannel.toString().split("=")[2].substring(1).replace(']', ' ').replaceAll(" ", "");
    }
}
