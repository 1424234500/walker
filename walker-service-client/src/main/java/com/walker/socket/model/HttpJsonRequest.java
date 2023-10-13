package com.walker.socket.model;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Lilinfeng
 * @version 1.0
 * @date 2014年3月1日
 */
@Data
@Accessors(chain = true)@AllArgsConstructor
public class HttpJsonRequest {
    private FullHttpRequest request;
    private Object body;

}