package com.walker.demo.guava;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.walker.core.util.Tools;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * HashSet中，然后利用HashSet的特性进行判断。它只花费O(1)的时间。但是，该方法消耗的内存空间很大，就算只有1亿个URL，每个URL只算50个字符，就需要大约5GB内存。
 * 冲突无法避免，这就带来了误判。理想中的算法总是又准确又快捷，但是现实中往往是“一地鸡毛”。我们真的需要100%的正确率吗？
 * 如果需要，时间和空间的开销无法避免；如果能够忍受低概率的错误，就有极大地降低时间和空间的开销的方法
 * <p>
 * 网络爬虫，还有处理缓存击穿和避免磁盘读取
 * <p>
 * <p>
 * 布隆过滤器
 * 多hash状态按位设置命中
 * <p>
 * 一 我们往过滤器里放一百万个数，然后去验证这一百万个数是否能通过过滤器，目的是校验是坏人是否一定被抓。
 * 二 我们另找1万个不在这一百万范围内的数，去验证漏网之鱼的概率，也就是布隆过滤器的误伤情况。
 * <p>
 * 位数组 预计容量,容错率 -> 容器长度,hash个数 guava 性能!
 * put过 则一定命中
 * 没put过 可能命中
 * <p>
 * 预热
 * 定期更新
 * <p>
 * guava
 * <p>
 * 自实现 	分布式
 * redis
 * set walker abc
 * 位数组 按位修改
 * setbit walker 6 1
 * setbit walker 7 1
 * get walker
 * bbc
 */
public class BloomFilterTest {

	// 预计元素个数
	private static final long size = 100 * 10000;
	private static final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), size, 0.01);

	public static void main(String[] args) {
		for (int i = 0; i < size; i++) {
			bloomFilter.put("key" + i);
		}
		List<String> inButNo = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			if (!bloomFilter.mightContain("key" + i)) {
				Tools.out("在缓存却没命中" + "key" + i);
				inButNo.add("key" + i);
			}
		}

		List<String> noButIn = new ArrayList<>();
		for (int i = (int) (size + 10000); i < size + 20000; i++) {
			if (bloomFilter.mightContain("key" + i)) {
				noButIn.add("key" + i);
				Tools.out("不在缓存却命中：" + noButIn.size());

			}
		}
		Tools.out("在缓存却没命中：" + inButNo.size());
		Tools.out("不在缓存却命中：" + noButIn.size());
	}


}

