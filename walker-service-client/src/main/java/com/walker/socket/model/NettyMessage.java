package com.walker.socket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年3月14日
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyMessage {
	private Header header;
	private Object body;
}