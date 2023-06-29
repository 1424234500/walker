package com.walker.demo;

import com.google.common.base.Objects;
import com.google.common.base.*;
import com.google.common.cache.*;
import com.google.common.collect.*;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.math.IntMath;
import com.google.common.util.concurrent.*;
import com.walker.core.util.TimeUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * guava常用整理
 */
public class GuavaTest {
    LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            //回收策略 仅在 write 写操作时触发 做检测处理 避免守护线程监控处理时竞争锁 偶尔在 read 读操作时触发
            //可选择自己线程调度显示清除

            //缓存key数量 容量回收
//            .maximumSize(1000)

            //自定义权重到达上限回收 如 字节数 内存
            .maximumWeight(2000)
            .weigher(new Weigher<String, String>() { //权重定义
                public int weigh(String key, String value) {
                    return value.length();
                }
            })

            //引用回收 性能影响 建议使用更有性能预测性的容量回收  值的缓存用==而不是equals比较值
            .softValues()

            //定期回收 写入 访问时间
            .expireAfterAccess(20, TimeUnit.SECONDS)
            .expireAfterWrite(20, TimeUnit.SECONDS)

            //定期自动刷新 实质为 当 read 读时 已经过期的且超过自动刷新时间 就自动刷新? 重载 reload 可异步
            .refreshAfterWrite(5, TimeUnit.SECONDS)

            //自定义时间源(多倍系统时间流速 时间加速器) 避免系统时钟 等待测试过期
            .ticker(new Ticker() {
                @Override
                public long read() {
                    //时间每过1秒 这里涨n秒
                    long now = System.currentTimeMillis();
                    long ymdhm = TimeUtil.format(TimeUtil.getTime(now, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
                    long secondAndMill = now - ymdhm;
                    return ymdhm + secondAndMill * 10; //10倍秒数增长
                }
            })
            //移除操作的监听器 可用于如 数据库连接缓存的 移除aop断开连接 清理上下文
            .removalListener(new RemovalListener<String, String>() {
                public void onRemoval(RemovalNotification<String, String> removal) {
                    //抛出的任何异常都会在记录到日志后被丢弃[swallowed]
                    out("cache removalListener onRemoval " + removal.getKey() + " -> " + removal.getValue() + " except " + removal.getCause());
                }
            })

            //开启统计
            .recordStats()

            .build(
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            return key.toUpperCase();
                        }

                        @Override
                        public Map<String, String> loadAll(Iterable<? extends String> keys) throws Exception {
                            Map<String, String> map = new HashMap<>();
                            map.put("hello1", "this is by loadAll");
                            return map;
                        }
                    });

    GuavaTest() throws Exception {
//        数学运算
        out("精度二进制运算丢失问题");
        out(Float.toString(2.0f), Float.toString(1.8f));
        out("2f - 1.8f   = " + (2f - 1.8f));
        out("2.0f - 1.8f = " + (2.0f - 1.8f));
        out("2f - 1f     = " + (2f - 1f));
        out("2.0d - 1.8d = " + (2.0d - 1.8d));
        out("2d - 1.8d = " + (2d - 1.8d));
        out("BigDecimal = " + (new BigDecimal(2.0f).subtract(new BigDecimal(1.8f))).doubleValue());
        out("BigDecimal Float.toString = " + (new BigDecimal(Float.toString(2.0f)).subtract(new BigDecimal(Float.toString(1.8f)))).doubleValue());


        out("math google 溢出检查运算 避免忽略坑 IntMath LongMath BigIntegerMath [ log2 log10 sqrt   ]");
        out("+ Integer.MAX_VALUE +  1", IntMath.checkedAdd(Integer.MAX_VALUE - 1, 1));
        out("/ 实数运算取舍 1 / 0", IntMath.divide(1, 0, RoundingMode.FLOOR));
//        最大公约数	gcd(int, int)
//        取模	mod(int, int)
//        取幂	pow(int, int)
//        是否2的幂	isPowerOfTwo(int)
//        阶乘*	factorial(int)
//        二项式系数*	binomial(int, int)


        Objects.equal("a", "a"); // returns true
        Objects.equal(null, "a"); // returns false
        Objects.equal("a", null); // returns false
        Objects.equal(null, null); // returns true
        Objects.equal(null, "null"); // returns false

        Objects.hashCode("aaa", "bbb");
        java.util.Objects.hash("aaa", "bbb");


//        // Returns "ClassName{x=1}"
//        Objects.toStringHelper(this).add("x", 1).toString();
//        // Returns "MyObject{x=1}"
//        Objects.toStringHelper("MyObject").add("x", 1).toString();

        //条件检查快速失败
        int i = 0, j = 1;
        Preconditions.checkArgument(i >= 0, "Argument was %s but expected nonnegative", i);
        Preconditions.checkArgument(i < j, "Expected i < j, but %s > %s", i, j);

        //不可变集合
//        当对象被不可信的库调用时，不可变形式是安全的；
//        不可变对象被多个线程调用时，不存在竞态条件问题
//        不可变集合不需要考虑变化，因此可以节省时间和空间。所有不可变的集合都比它们的可变形式有更好的内存利用率（分析和测试细节）；
//        不可变对象因为有固定不变，可以作为常量来安全使用。
//        创建对象的不可变拷贝是一项很好的防御性编程技巧
        ImmutableSet<String> immutableSet = ImmutableSet.<String>builder()
                .add("01")
                .add("02")
                .build();
        out("immutableSet01=" + immutableSet);
        immutableSet = ImmutableSet.of("foo", "bar", "baz");
        out("immutableSet02=" + immutableSet);

        //工厂构造 sets, maps, collections2, ...tables
        Lists.newArrayList("1", "2", "3");
        Lists.newArrayListWithCapacity(100);

        //////////////////////
        //新集合类型
        /////////////////////

        Multiset<String> multiset = HashMultiset.create(Arrays.asList("a", "a", "a", "b", "b", "c"));
        out("去重计数 set || Map<Key, Count> multiset=" + multiset);

        Multimap<String, Object> multimap = HashMultimap.create();
        multimap.put("a", "ao01");
        multimap.put("a", "ao02");
        multimap.put("b", "bo01");
        multimap.put("b", "bo02");
        multimap.put("c", "co");
        out("主键分组 Map<Key, Set<Value>> multimap=" + multimap);

        //Map<Key, Value>
        //Map<Value, Key>
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("a", 0);
        biMap.put("b", 1);
        biMap.put("c", 2);
        out("双向map 值唯一 biMap.get(\"a\")=" + biMap.get("a"));
        out("双向map 值唯一 biMap.inverse().get(0)=" + biMap.inverse().get(0));

        Table<String, String, String> table = HashBasedTable.create();
        table.put("r1", "c1", "r1c1");
        table.put("r1", "c2", "r2c2");
        table.put("r2", "c1", "r2c1");
        out("行列二维数组 多键索引 Map<FirstName, Map<LastName, Person>> " + table);
        out(" 行 r1 " + table.row("r1"));
        out(" 列 c1 " + table.column("c1"));


        out("区间检测", Range.closed(3, 5).isConnected(Range.open(5, 10))); // returns true
        out("区间 连接检测", Range.closed(1, 5).isConnected(Range.closed(3, 10))); // returns true
        out("区间运算 包含", Range.closed(1, 5).encloses(Range.closed(3, 10))); // returns true
        out("区间运算 交集 intersection", Range.closed(1, 5).intersection(Range.closed(3, 10))); // returns true
        out("区间运算 最小和区间 若连接则并集 span", Range.closed(1, 5).span(Range.closed(3, 10))); // returns true


        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10)); // {[1,10]}
        rangeSet.add(Range.closedOpen(11, 15));//不相连区间:{[1,10], [11,15)}
        rangeSet.add(Range.closedOpen(15, 20)); //相连区间; {[1,10], [11,20)}
        rangeSet.add(Range.openClosed(0, 0)); //空区间; {[1,10], [11,20)}
        rangeSet.remove(Range.open(5, 10)); //分割[1, 10]; {[1,5], [10,10], [11,20)}
        out("区间 差集 并集 合并 rangeSet " + rangeSet);

        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(1, 10), "foo"); //{[1,10] => "foo"}
        rangeMap.put(Range.open(3, 6), "bar"); //{[1,3] => "foo", (3,6) => "bar", [6,10] => "foo"}
        rangeMap.put(Range.open(10, 20), "foo"); //{[1,3] => "foo", (3,6) => "bar", [6,10] => "foo", (10,20) => "foo"}
        rangeMap.remove(Range.closed(5, 11)); //{[1,3] => "foo", (3,5) => "bar", (11,20) => "foo"}
        out("区间 离散 合并 rangeMap " + rangeMap);


        AddLoggingList<String> addLoggingList = new AddLoggingList<>();
        addLoggingList.add("hello");


        testCache();

        testFuture();

        testStringJoin();

        testFiles();

        out("Hashing.crc32", Hashing.crc32().hashBytes("aaaa".getBytes(Charsets.UTF_8)));
        out("Hashing.md5", Hashing.md5().hashBytes("aaaa".getBytes(Charsets.UTF_8)));
        out("Hashing.sha256", Hashing.sha256().hashBytes("aaaa".getBytes(Charsets.UTF_8)));
        out("Hashing.murmur3_32", Hashing.murmur3_32().hashBytes("aaaa".getBytes(Charsets.UTF_8)));

    }

    public static void main(String[] argv) throws Exception {
        new GuavaTest();
    }

    public static void out(Object... objects) {
        System.out.println(Arrays.toString(objects));
    }

    void testFiles() throws IOException {

        Files.createParentDirs(new File("/test/hello.txt.c"));
        Files.getFileExtension("/test/hello.txt.c");
        Files.getNameWithoutExtension("/test/hello.txt.c");
        Files.copy(new File("from"), new File("to"));
        Files.move(new File("from"), new File("to"));
        for (File file : Files.fileTraverser().depthFirstPostOrder(new File("/home/walker"))) {

        }
    }

    void testCache() throws ExecutionException {
        cache.put("hello", "this is by put");
        //peek 模式 不影响正常所有流程
        cache.asMap()
                .putIfAbsent("hello998", "this is by asMap 能修改缓存 不能保证缓存项被原子地加载到缓存");

        out("cache.get hello " + cache.get("hello"));
        for (int i = 0; i < Math.random() * 10; i++) {
            out("cache.get hello1 " + cache.get("hello1"));
        }
        out("cache.getUnchecked hello2 " + cache.getUnchecked("hello2"));

        //如果有缓存则返回；否则运算、缓存、然后返回 用于指定每一个缓存值自定义获取
        out("cache.getCallable hello3 " + cache.get("hello3", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "this is by callable 灵活 hello3";
            }
        }));

        //显示过期标记
        cache.invalidateAll();
        //触发清理 立即
        cache.cleanUp();

        out("recordStats " + cache.stats());

    }

    void testFuture() throws InterruptedException {
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        ListenableFuture<String> future = pool.submit(new Callable<String>() {
            public String call() throws InterruptedException {
                Thread.sleep(1000);
                return "hello wait 1 s";
            }
        });
        Futures.addCallback(future, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                out("call onSuccess " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                out("call onFailure " + t);
            }
        }, pool);
        Thread.sleep(1000);
        pool.shutdownNow();
    }

    void testStringJoin() {
        Joiner joiner = Joiner.on(", ").skipNulls().useForNull("!EOF!");
        out("joiner.join(\"Harry\", null, \"Ron\", \"Hermione\")", joiner.join("Harry", null, "Ron", "Hermione"));

        out("\",a,,b,\".split(\",\") = ? ", ",a,,b,".split(","));
//        omitEmptyStrings()	从结果中自动忽略空字符串
//        trimResults()	移除结果字符串的前导空白和尾部空白
//        trimResults(CharMatcher)	给定匹配器，移除结果字符串的前导匹配字符和尾部匹配字符
//        limit(int)	限制拆分出的字符串数量
        out(Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split("foo,bar,,   qux"));
        byte[] bytes = "hello ".getBytes(Charsets.UTF_8);

//        大小写 驼峰转换
//        LOWER_CAMEL	lowerCamel
//        LOWER_HYPHEN	lower-hyphen
//        LOWER_UNDERSCORE	lower_underscore
//        UPPER_CAMEL	UpperCamel
//        UPPER_UNDERSCORE	UPPER_UNDERSCORE
        out(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "CONSTANT_NAME")); // returns "constantName"

    }

    public int compareTo(Map<String, String> from, Map<String, String> to) {
        return ComparisonChain.start()
                .compare(from.getOrDefault("a", "0"), to.getOrDefault("a", "0"))
                .compare(from.getOrDefault("b", "0"), to.getOrDefault("b", "0"), Ordering.natural().nullsLast())
                .result();
    }

    //装饰器 ForwardingList.get(int)实际上执行了delegate().get(int)。
    class AddLoggingList<E> extends ForwardingList<E> {
        final List<E> delegate = new ArrayList<>(); // backing list

        @Override
        protected List<E> delegate() {
            return delegate;
        }

        @Override
        public void add(int index, E elem) {
            out("装饰 list " + elem);
            super.add(index, elem);
        }

        @Override
        public boolean add(E elem) {
            return standardAdd(elem); // 用add(int, E)实现
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return standardAddAll(c); // 用add实现
        }
    }

}
