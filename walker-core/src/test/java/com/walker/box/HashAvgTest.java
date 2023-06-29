package com.walker.box;

import com.walker.core.mode.HashAvg;
import junit.framework.TestCase;

public class HashAvgTest extends TestCase {
    public void testFNVHash() {
        HashAvg ch = new HashAvg()
                .setNumVirtualNode(32)
                .addPhysicalNode("192.168.1.101")
                .addPhysicalNode("192.168.1.102")
                .addPhysicalNode("192.168.1.103")
                .addPhysicalNode("192.168.1.104");
        // 初始情况
        ch.dumpObjectNodeMap("初始情况", 0, 65536);

        // 删除物理节点
        ch.removePhysicalNode("192.168.1.103");
        ch.dumpObjectNodeMap("删除物理节点", 0, 65536);

        // 添加物理节点
        ch.addPhysicalNode("192.168.1.108");
        ch.dumpObjectNodeMap("添加物理节点", 0, 65536);
    }
}