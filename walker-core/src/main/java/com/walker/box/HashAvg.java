package com.walker.box;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash
 */
public class HashAvg {


    //虚拟节点
    int numVirtualNode = 32; // 物理节点至虚拟节点的复制倍数
    TreeMap<Long, String> mapVirtualNode = new TreeMap<>(); // 哈希值 => 物理节点

    // 根据物理节点，构建虚拟节点映射表
    public HashAvg() {
    }

    // 32位的 Fowler-Noll-Vo 哈希算法
    // https://en.wikipedia.org/wiki/Fowler–Noll–Vo_hash_function
    public static Long FNVHash(String key) {
        final int p = 16777619;
        Long hash = 2166136261L;
        for (int idx = 0, num = key.length(); idx < num; ++idx) {
            hash = (hash ^ key.charAt(idx)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    public static void main(String[] args) {
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

    /**
     * 一致性哈希和随机树：用于缓解万维网上热点的分布式缓存协议
     * 容错性和可扩展性 缓存集群分片时 都会涉及 多服务器节点 增加 删除节点后
     * 如何保持旧的节点依然受理相同请求
     * 如何保持新的节点列表负载均衡 数据倾斜
     * 取模法进行缓存时 	n%4 -> n%3   n%4 -> n%5	大多数数据的映射关系都会失效
     * <p>
     * 一致性hash
     * <p>
     * 是一个待分配hash的key 多次hash映射多个numVirtualNode个节点（穿插分布均匀） 放到一个较大的map中（hash环 虚拟节点补充数量 数据倾斜问题）  多hashcode映射相同key
     * 缓存kv1 根据map(kv1)拿到相邻的hashcode的key 缓存kv2
     * 删除节点2后
     * 新的kv1 根据map(kv1)依然拿到相邻的hashcode的key
     * 新的kv2 根据map(kv2)拿到相邻的hashcode的key
     * <p>
     * 增加节点3后 插值区间 只影响相邻的下个节点  需要重新增加缓存到节点3 而其他的节点都不会受影响。
     * <p>
     * hash记录的顺势取相邻hash的下一个 并虚拟节点解决数据倾斜
     * <p>
     * 布隆过滤器
     * 是一个待确认的key 多次hash 各自放到 有限的 位槽 array		相同key映射多个hashcode
     * 新的key1若已经处理过 则多次hash都均能命中
     * 若未处理过 则可能命中 误判率（多hash次数和槽位数决定）
     * <p>
     * 主要目的是为了全key 海量key 放入有限的位槽 （允许误判率）
     */
    public HashAvg addPhysicalNode(String nodeIp) {
        for (int idx = 0; idx < numVirtualNode; ++idx) {
            long hash = FNVHash(nodeIp + "#" + idx);
            mapVirtualNode.put(hash, nodeIp);
        }
        return this;
    }

    // 删除物理节点
    public HashAvg removePhysicalNode(String nodeIp) {
        for (int idx = 0; idx < numVirtualNode; ++idx) {
            long hash = FNVHash(nodeIp + "#" + idx);
            mapVirtualNode.remove(hash);
        }
        return this;
    }

    // 查找对象映射的节点
    public String getObjectNode(String object) {
        long hash = FNVHash(object);
        SortedMap<Long, String> tailMap = mapVirtualNode.tailMap(hash); // 所有大于 hash 的节点   该节点已经被删除 顺势拿到相邻的下一个节点
        Long key = tailMap.isEmpty() ? mapVirtualNode.firstKey() : tailMap.firstKey();
        return mapVirtualNode.get(key);
    }

    // 统计对象与节点的映射关系
    public void dumpObjectNodeMap(String label, int objectMin, int objectMax) {
        // 统计
        Map<String, Integer> objectNodeMap = new TreeMap<>(); // IP => COUNT
        for (int object = objectMin; object <= objectMax; ++object) {
            String nodeIp = getObjectNode(Integer.toString(object));
            Integer count = objectNodeMap.get(nodeIp);
            objectNodeMap.put(nodeIp, (count == null ? 0 : count + 1));
        }

        // 打印
        double totalCount = objectMax - objectMin + 1;
        System.out.println("======== " + label + " ========");
        for (Map.Entry<String, Integer> entry : objectNodeMap.entrySet()) {
            long percent = (int) (100 * entry.getValue() / totalCount);
            System.out.println("IP=" + entry.getKey() + ": RATE=" + percent + "%");
        }
    }

    public int getNumVirtualNode() {
        return numVirtualNode;
    }

    public HashAvg setNumVirtualNode(int numVirtualNode) {
        this.numVirtualNode = numVirtualNode;
        return this;
    }
}