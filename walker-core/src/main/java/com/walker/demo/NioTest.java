package com.walker.demo;

import com.walker.util.FileUtil;
import com.walker.util.ThreadUtil;
import com.walker.util.TimeUtil;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

public class NioTest {


    NioTest() throws Exception {
        fileChannelAndBuffer();
        channelAndSelector();
        pipe();
    }

    public static void main(String[] argv) throws Exception {
        new NioTest();
    }

    public static String channelRead(ReadableByteChannel channel) throws IOException {
        StringBuilder res = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();
        int size = 0;
        while ((size = channel.read(byteBuffer)) != -1) { //Pipe.SourceChannel 永不结束-1
            byteBuffer.flip();
            StringBuilder item = new StringBuilder();
            while (byteBuffer.hasRemaining()) { //若buffer里有东西
                item.append((char) byteBuffer.get());    //出栈?
            }
            System.out.println(item);
            res.append(item);
            //清空 or 转换 为入栈模式?
            byteBuffer.clear();
//            byteBuffer.flip();
        }
        return res.toString();
    }

    /**
     * 数据是从通道 channel 读入缓冲区 buffer ，从缓冲区 buffer 写入到通道 channel 中
     */
    void fileChannelAndBuffer() throws IOException {
        String tempFile = "temp.file.txt";
        FileUtil.saveAs("abcdefghijklmnopqrstuvwxyz1234567890", tempFile, false);
        RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        //一段连续内存 数组 依靠几个int数字来标记当前位置 总长度 起点位置
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
//        CharBuffer buf = CharBuffer.allocate(1024);
        System.out.println("read>>>>>>>>>>>>>>\n" + channelRead(fileChannel));

        //手动塞到 buffer
        byteBuffer.put("== put ".getBytes());
        byteBuffer.flip();
        fileChannel.write(byteBuffer); //全部读取 buffer 到 channel
        byteBuffer.rewind(); //重置读取状态
        byteBuffer.get(); //读取掉了两个
        byteBuffer.get();
        //仍有未读的数据，且后续还需要这些数据，但是此时想要先写些数据
        byteBuffer.compact();
        byteBuffer.put("compact".getBytes()); //读取两个后 再写入一些
        byteBuffer.flip();
        fileChannel.write(byteBuffer); //读取 buffer 到 channel

        System.out.println(">>>>>>>>>>>>>> file is >>>> \n" + FileUtil.readByLines(tempFile, null, null));


        //只比较Buffer中的未读剩余元素
        byteBuffer.equals(ByteBuffer.wrap("hello".getBytes()));
        byteBuffer.compareTo(ByteBuffer.wrap("hello".getBytes()));

        ByteBuffer byteBufferHeader = ByteBuffer.allocate(10);
        ByteBuffer[] byteBuffers = new ByteBuffer[]{byteBufferHeader, byteBuffer};

        //Scattering Reads 从一个 channel 读取数据依次填充到多个 buffer
        fileChannel.read(byteBuffers);
        //Gathering Writes 从多个 buffer 依次写入到一个 channel
        fileChannel.write(byteBuffers);

        fileChannel.force(true); //刷盘 权限信息附加 metaData
        fileChannel.close();

        randomAccessFile.close();
    }

    /**
     * selector 处理多个 通道 channel
     */
    void channelAndSelector() throws IOException, InterruptedException {

    }

    /**
     * pipe 管道 多线程单项数据传输
     */
    void pipe() throws IOException {
        Pipe pipe = Pipe.open();
        Pipe.SinkChannel sinkChannel = pipe.sink();
        Pipe.SourceChannel sourceChannel = pipe.source();

        ThreadUtil.scheduleAtFixedRate(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                String make = ("hello pipe " + TimeUtil.getTimeYmdHms());
                System.out.println(Thread.currentThread().getName() + " sinkChannel make " + make);
                ByteBuffer byteBuffer = ByteBuffer.wrap(make.getBytes());
                while (byteBuffer.hasRemaining()) {
                    sinkChannel.write(byteBuffer);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        //api介绍 耗时超过周期时不会并发 会等待上次结束后立即执行 耗时小于周期 则会固定频率执行
        ThreadUtil.scheduleAtFixedRate(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " sourceChannel read >>>>>   " + channelRead(sourceChannel));
            }
        }, 1, 1, TimeUnit.SECONDS);

    }


}
