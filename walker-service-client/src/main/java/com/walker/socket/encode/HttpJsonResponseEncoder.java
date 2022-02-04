package com.walker.socket.encode;

import com.walker.core.encode.JsonFastUtil;
import com.walker.socket.model.HttpJsonResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

import java.nio.charset.Charset;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by carl.yu on 2016/12/16.
 */
public class HttpJsonResponseEncoder extends MessageToMessageEncoder<HttpJsonResponse> {
	@Override
	protected void encode(ChannelHandlerContext ctx, HttpJsonResponse msg, List<Object> out) throws Exception {
		//编码
		ByteBuf body = Unpooled.copiedBuffer(JsonFastUtil.toString(msg.getResult()), Charset.forName("utf-8"));
		FullHttpResponse response = msg.getHttpResponse();
		if (response == null) {
			response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, body);
		} else {
			response = new DefaultFullHttpResponse(msg.getHttpResponse().protocolVersion(), msg.getHttpResponse().status(), body);
		}
		response.headers().set(CONTENT_TYPE, "text/json");
		HttpUtil.setContentLength(response, body.readableBytes());
		out.add(response);
	}


}