package com.walker.socket.client.http;

import com.walker.core.util.ClassUtil;
import com.walker.socket.model.HttpJsonRequest;
import com.walker.socket.model.HttpJsonResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by carl.yu on 2016/12/16.
 */
public class HttpJsonClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接上服务器...");
        HttpJsonRequest request = new HttpJsonRequest(null,
                new ClassUtil.Model()
                .setClassName("com.walker.socket.server.http.HttpJsonServerController")
                .setConstructorArgs("cr")
                .setMethodName("hello")
                .setMethodArgs("info test")
        );
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpJsonResponse httpJsonResponse = (HttpJsonResponse) msg;
        System.out.println(msg.getClass().getName());
        System.out.println("接收到了数据..." + httpJsonResponse.getResult());
    }

    /*@Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpJsonResponse msg) throws Exception {
        System.out.println("The client receive response of http header is : "
                + msg.getHttpResponse().headers().names());
        System.out.println("The client receive response of http body is : "
                + msg.getResult());
    }*/


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}