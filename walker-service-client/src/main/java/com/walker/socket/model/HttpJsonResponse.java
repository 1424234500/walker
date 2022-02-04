package com.walker.socket.model;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @date 2014年3月1日
 */
@Data
@AllArgsConstructor
public class HttpJsonResponse {
    private FullHttpResponse httpResponse;
    private Object result;
}