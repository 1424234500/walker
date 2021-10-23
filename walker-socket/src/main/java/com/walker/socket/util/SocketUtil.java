package com.walker.socket.util;

import com.walker.util.LangUtil;
import com.walker.util.Tools;
import io.netty.channel.ChannelHandlerContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketUtil {

/**
 * 包协议
 * 4个字节 长度 size 
 * 正文\r
 *
 */


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
                    byte[] buffer = new byte[2048];
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
        if (!Tools.notNull(jsonstr)) return;
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
    public static void sendImpl(SocketChannel socket, String jsonstr) throws Exception {
        if (!Tools.notNull(jsonstr)) return;

        byte[] bytes = jsonstr.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(bytes.length);
        sizeBuffer.flip();
        while (sizeBuffer.hasRemaining()) {
            socket.write(sizeBuffer);
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(bytes);
        buffer.flip();
        while (buffer.hasRemaining()) {
            socket.write(buffer);
        }
        socket.finishConnect();
    }

    /**
     * socket nio 非阻塞模式读取字节包
     */
    public static String readImpl(SocketChannel socket) throws Exception {
        String res = "";

        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.clear();
        int read = socket.read(sizeBuffer);    //尝试读取数据流 的头4个字节<int> 读取长度 -1表示读取到了数据流的末尾了；
        if (read != -1) {    //若有数据<0.2条数据 1条数据 3.2条数据>
            sizeBuffer.flip();
            int size = sizeBuffer.getInt();    //头4个字节 int 大小 int = 4byte = 32bit

            int readCount = 0;
            byte[] b = new byte[1024];
            ByteBuffer buffer = ByteBuffer.allocate(1024);
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
        if (!Tools.notNull(jsonstr)) return;

        byte[] bytes = jsonstr.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(bytes.length);
        sizeBuffer.flip();
        while (sizeBuffer.hasRemaining()) {
            socket.write(sizeBuffer);
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(bytes);
        buffer.flip();
        while (buffer.hasRemaining()) {
            socket.write(buffer);
        }
//		socket.finishConnect();
    }


    /**
     * socket nio 非阻塞模式读取字节包
     */
    public static String readImpl(AsynchronousSocketChannel socket) throws Exception {
        String res = "";

        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.clear();
//		socket.read
        int read = 0;//socket.read(sizeBuffer);	//尝试读取数据流 的头4个字节<int> 读取长度 -1表示读取到了数据流的末尾了；
        if (read != -1) {    //若有数据<0.2条数据 1条数据 3.2条数据>
            sizeBuffer.flip();
            int size = sizeBuffer.getInt();    //头4个字节 int 大小 int = 4byte = 32bit

            int readCount = 0;
            byte[] b = new byte[1024];
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder sb = new StringBuilder();
            while (readCount < size) {  //读取已知长度消息内容 异步读取 死循环 直到读够目标字节数
                buffer.clear();
                read = socket.read(buffer).get();
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
        return socketChannel.toString().split("=")[2].substring(1).replace(']', ' ').replaceAll(" ", "");
    }
}
