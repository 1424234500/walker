package com.walker.socket.server.http;

import com.walker.socket.model.HttpJsonRequest;
import com.walker.socket.model.HttpJsonResponse;
import com.walker.util.ClassUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by carl.yu on 2016/12/16.
 */
public class HttpJsonServerHandler extends SimpleChannelInboundHandler<HttpJsonRequest> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpJsonRequest msg) throws Exception {
		HttpRequest request = msg.getRequest();
		ClassUtil.Model order = (ClassUtil.Model) msg.getBody();
		if(order == null){
			order = new ClassUtil.Model().setClassName("com.walker.socket.server.http.HttpJsonServerController").setMethodName("html");
		}
		Object res = dobusiness(order);
		ChannelFuture future = ctx.writeAndFlush(new HttpJsonResponse(null, res));
		if (!HttpUtil.isKeepAlive(request)) {
			future.addListener(future1 -> ctx.close());
		}
	}

	/**
	 * 反射调用目标方法
	 * @param model
	 * @return
	 */
	private Object dobusiness(ClassUtil.Model model) {
		return model.action();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		if (ctx.channel().isActive()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}

	private static void sendError(ChannelHandlerContext ctx,
								  HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				status, Unpooled.copiedBuffer("失败: " + status.toString()
				+ "\r\n", CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}