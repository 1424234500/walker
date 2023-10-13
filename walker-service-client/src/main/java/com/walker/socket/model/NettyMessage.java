package com.walker.socket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年3月14日
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class NettyMessage {
	private Header header;
	private Object body;
}