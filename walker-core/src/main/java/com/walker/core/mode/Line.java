package com.walker.core.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据结构  线 权重
 *
 * 节点之间的距离 关系
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Line {
    String name = "";
	int num = 1;
	int value = 1;

}

