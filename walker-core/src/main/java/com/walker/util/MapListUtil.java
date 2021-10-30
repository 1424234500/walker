package com.walker.util;

import java.util.*;


/**
 * map list常用工具
 *
 * @author Walker
 */
public class MapListUtil {

    public static void main(String[] argc) {
        Tools.out("-------测试list set map相关区别 争议点");

        ArrayList<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> map = getMap().put("key1", "value1").put(null, "value null").put("value null", null).build();
        Map<String, Object> map2 = getMap().put("key2", "value2").build();

        Map<String, Object> map22 = getMap().put("key2", "value2").build();
        Map<String, Object> map3 = map;
        Tools.out("map.keySet null键唯一 键集合", map.keySet());
        Tools.out("map.entrySet 键值集合", map.entrySet());
        //for (Map.Entry<String, Object> entry : map.entrySet())

        list.add(map);
        list.add(map2);
        Tools.out("hash map键值可null 无序", list);
        Tools.out("map==map", map == map, "map.equals(map)", map.equals(map),
                "map==map2", map == map2, "map.equals(map2)", map.equals(map2),
                "map==map3", map == map3, "map.equals(map3)", map.equals(map3),
                "map2==map22", map2 == map22, "map2.equals(map22) equals有效值判断=", map2.equals(map22)
        );
        list.add(map);
        list.add(map3);
        Tools.out("list一种数组 可重复键值 可null add对象的引用唯一", list);

        HashSet<Map> set = new HashSet<Map>();
        set.add(map);
        set.add(map2);
//		Tools.out("set 不同map", set);
        set.add(map);
        set.add(map3);
        set.add(map22);
        set.add(null);
        Tools.out("set 一种数组(list) 允许一个null 相同引用(map) add对象的value equals判断不重复  重复覆盖", set);

        HashMap<String, Object> hmap = new HashMap<>();
        hmap.put(null, "valueNull");
        hmap.put("keynull", null);
        hmap.put("key1", "value1");

        Tools.out("hash map 键值可null HashMap去掉了HashTable的contains方法，但是加上了containsValue()和containsKey()方法", hmap);

        Hashtable<String, Object> htable = new Hashtable<>();
//		htable.set(null, "valueNull");
//		htable.set("keynull", null);
        htable.put("key1", "value1");

        Tools.out("hash table 键值不能null 。HashTable sync 锁", htable);

        Tools.out("-----------");

        List<Map<String, Object>> li = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> mm = getMap()
                    .put("id", "id-" + i)
                    .put("name", "name-" + i)
                    .build();
            li.add(0, mm);
        }
        Tools.out(li);
        li = turnListMap(li);
        Tools.out(li);


    }


    /**
     * 构造echarts数据结构
     * <p>
     * <p>
     * <p>
     * 调用案例
     * <p>
     * //			avg		max		sum
     * //2010		1		20		200
     * //2011		2		21		203
     * //2012		2		10		122
     * //2013		3		15		27
     * //y轴三条线 avg max sum
     * //x轴时间戳	2010 2011 2012 2013
     * //
     * //		指标按行查询出来 首列为x轴坐标, 后续每列为每一条线 指标
     * List<Map<String, Object>> listDb = statisticsMapper.findAction(from, to);
     * <p>
     * //    每条线名称
     * List<String> lineNames = Arrays.asList("平均值", "最大值", "和");
     * //    每条线类型
     * List<String> lineTypes = Arrays.asList("bar", "bar", "bar");
     * //    每条线堆叠类型
     * List<String> lineStacks = Arrays.asList("1", "1", "2");
     * <p>
     * Map option = MapListUtil.makeEchartOption("Action概览", "W_LOG_TIME", ""
     * , listDb, lineNames, lineTypes, lineStacks);
     *
     * @param title
     * @param subtitle
     * @param sublink
     * @param listDb     数据库数据	行数代表采样点x轴数值的数量	(列数-1)代表采样点对于的多个y值 线的数量
     * @param lineNames  每条线名称			默认 Y1, Y2...Yn
     * @param lineTypes  每条线类型			默认 bar, bar...bar
     * @param lineStacks 每条线堆叠类型	默认 无 1, 1...1
     */
    public static Bean makeEchartOption(String title, String subtitle, String sublink, List<Map<String, Object>> listDb, List<String> lineNames, List<String> lineTypes, List<String> lineStacks) {

//        矩阵转置
        List<List<String>> list = MapListUtil.toArrayAndTurn(listDb);
//		  第一行代表x轴
//		  第二行代表y1 值1 线1
        List<String> listXs = list.size() > 0 ? list.get(0) : new ArrayList<>();
        List<Bean> series = new ArrayList();
//		构造各线属性 默认值
        if (lineNames == null) {
            lineNames = new ArrayList<>();
        }
        for (int i = lineNames.size(); i < list.size() - 1; i++) {    //六行 则 五列	需五个属性配置
            lineNames.add("Y" + i);
        }
        if (lineTypes == null) {
            lineTypes = new ArrayList<>();
        }
        for (int i = lineTypes.size(); i < list.size() - 1; i++) {    //六行 则 五列	需五个属性配置
            lineTypes.add("bar");
        }
        if (lineStacks == null) {
            lineStacks = new ArrayList<>();
        }
        for (int i = lineStacks.size(); i < list.size() - 1; i++) {    //六行 则 五列	需五个属性配置
            lineStacks.add(lineNames.get(i));
        }

        //指标 结构 [ {  name: '百度', type: 'bar', stack: '搜索引擎', data: [620, 732, 701, 734, 1090, 1130, 1120]  }, ]
        for (int i = 0; i < list.size() - 1; i++) {
            List<String> line = list.get(i + 1);
            series.add(new Bean()
                    .set("name", lineNames.get(i))
                    .set("type", lineTypes.get(i))
                    .set("stack", lineStacks.get(i))
                    .set("data", line)
            );
        }

        Bean option = new Bean()
                .put("title", new Bean()
                        .put("text", title)
                        .put("subtext", subtitle)
                        .put("sublink", sublink)
                )
                .put("legend", new Bean()
                        .put("data", lineNames)
                )   //线 y轴值中文名列表
                .put("tooltip", new HashMap())
                .put("xAxis", Arrays.asList(
                                new Bean()
                                        .put("data", listXs)
                        )
                )     //x轴 坐标中文名列表
                .put("yAxis", Arrays.asList(new HashMap())) //若无报错YAxis 0 not found
                .put("series", series)      //指标 结构 [ {  name: '百度', type: 'bar', stack: '搜索引擎', data: [620, 732, 701, 734, 1090, 1130, 1120]  }, ]
                ;

        return option;
    }


    @SuppressWarnings({"rawtypes"})
    public static Map copy(Map<Object, Object> map) {
        return copy(map, map.keySet().toArray());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map copy(Map map, Object... keys) {
        Map res = new HashMap<>();
        for (Object key : keys) {
            res.put(key, map.get(key));
        }
        return res;
    }

    /**
     * 获取Map工厂build模式
     */
    public static MakeMap getMap() {
        return new MakeMap();
    }

    public static MakeMap map() {
        return new MakeMap();
    }

    /**
     * 获取ArrayList
     */
    public static MakeList getList() {
        return new MakeList();
    }

    public static MakeList array() {
        return new MakeList();
    }

    /**
     * 获取ArrayList
     */
    public static List<Map> getListMap() {
        return new ArrayList<>();
    }

    /**
     * 获取某键值索引第一个
     */
    public static int getCountListByName(List<Map<String, Object>> list, String name, String value) {
        if (list == null) return -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get(name).toString().equals(value)) {
                return i;
            }
        }
        return -1;

    }

    /**
     * List<Map> 转换为 List<Map<String, String>
     *
     * @param list
     */
    @SuppressWarnings("rawtypes")
    public static List<Map<String, String>> getList(List<Map> list) {
        List<Map<String, String>> res = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> map = new HashMap<String, String>(list.get(i));
                res.add(map);
            }
        }
        return res;
    }

    /**
     * 获取list<map>对应的二维数组的 某列
     */
    public static List<Object> getListCol(List<Map<String, Object>> list, int colIndex) {
        List<Object> res = new ArrayList<>();

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if (map == null) {
                    res.add(null);
                } else {
                    String[] ss = map.keySet().toArray(new String[0]);
                    if (ss.length > colIndex) {
                        res.add(map.get(ss[colIndex]));
                    } else {
                        res.add(null);
                    }
                }
            }
        }

        return res;
    }

    /**
     * 获取list<map>的第i行name列
     */
    public static String getList(List<Map<String, Object>> list, int i, String name) {
        return getList(list, i, name, "null");
    }

    /**
     * 获取list<map>的第i行name列 默认值
     */
    public static String getList(List<Map<String, Object>> list, int i, String name, String defaultValue) {
        if (list == null) return "list is null";
        if (i < 0) return "i < 0";
        if (i >= list.size()) return "i > list size";
        if (list.get(i).get(name) == null) {
            return defaultValue;
        } else {
            return getMap(list.get(i), name);
        }
    }

    public static String getMap(Map map, String name) {
        return getMap(map, name, "");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T getMap(Map map, Object key, T defaultValue) {
        if (map == null) return defaultValue;

        Object obj = map.get(key);
        T res = null;
        if (obj == null) obj = map.get(key.toString().toLowerCase());
        if (obj == null) obj = map.get(key.toString().toUpperCase());

        if (obj == null) {
            res = defaultValue;
        } else {
            if (defaultValue instanceof String) {
                res = (T) (obj.toString());
            } else if (obj instanceof String) {
                if (defaultValue instanceof Integer) {
                    res = (T) (new Integer(Integer.valueOf(obj.toString())));
                } else if (defaultValue instanceof Double) {
                    res = (T) (new Double(Double.valueOf(obj.toString())));
                } else {
                    res = (T) obj;
                }
            } else {
                res = (T) obj;
            }
        }

        return res;
    }


    /**
     * 转换Map<String, Object> -> Map<String, Double>
     *
     * @param map
     */
    public static Map<String, Double> map2sdmap(Map<String, Object> map) {
        Map<String, Double> res = new HashMap<String, Double>();
        for (String key : map.keySet()) {
            res.put(key, (Double) (map.get(key)));
        }
        return res;
    }

    /**
     * 转换Map<String, Object> -> Map<String, String>
     *
     * @param map
     */
    public static Map<String, String> map2ssmap(Map<String, Object> map) {
        Map<String, String> res = new HashMap<String, String>();
        for (String key : map.keySet()) {
            res.put(key, map.get(key).toString());
        }
        return res;
    }

    /**
     * 转换Map<String, Object> <- Map<String, String>
     *
     * @param map
     */
    public static Map<String, Object> map2map(Map<String, String> map) {
        Map<String, Object> res = new HashMap<>();
        for (String key : map.keySet()) {
            res.put(key, map.get(key));
        }
        return res;
    }

    /**
     * List<Map> 转换为 可读的String
     *
     * @param list
     */
    @SuppressWarnings("unchecked")
    public static String list2string(List<Map> list) {
        String res = "[ \n";
        for (Map<String, Object> map : list) {
            res += map.toString();
            res += "\n";
        }
        res += " ]";
        return res;
    }

    /**
     * map转值数组
     *
     * @param map
     */
    public static List<String> toValues(Map<String, Object> map) {
        List<String> res = new ArrayList<>();
        if (map != null) {
            for (Object value : map.values()) {
                res.add(String.valueOf(value));
            }
        }
        return res;
    }

    /**
     * 有序合并List
     *
     * @param list1
     * @param list2
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List listAdd(List list1, List list2) {
        if (list1 != null) {
            if (list2 != null) {
                for (int i = 0; i < list2.size(); i++) {
                    list1.add(list2.get(i));
                }
            }
        } else {
            list1 = list2;
        }

        return list1;
    }

    /**
     * 读取键值行列并转置
     *
     * @param list
     */
    public static List<List<String>> toArrayAndTurn(List<Map<String, Object>> list) {
        return turnRerix(toArray(list));
    }

    /**
     * 键值结果 行列值读取
     *
     * @param list
     */
    public static List<List<String>> toArray(List<Map<String, Object>> list) {
        List<List<String>> res = new ArrayList<>();

        if (list != null && list.size() > 0) {
            Set<?> set = list.get(0).keySet();
//			int colSize = set.size();
            int rowSize = list.size();

            for (int i = 0; i < rowSize; i++) {
                List<String> ll = new ArrayList<>();
                for (Object key : set) {
                    ll.add(getList(list, i, "" + key));
                }
                res.add(ll);
            }
        }

        return res;
    }

    /**
     * 二维数组行列转换
     *
     * @param list
     */
    public static List<List<String>> turnRerix(List<List<String>> list) {

        List<List<String>> res = new ArrayList<>();

        if (list != null && list.size() > 0) {
            int colSize = list.get(0).size();
            int rowSize = list.size();
            for (int i = 0; i < colSize; i++) {
                List<String> ll = new ArrayList<>();
                for (int j = 0; j < rowSize; j++) {
                    ll.add(list.get(j).get(i));
                }
                res.add(ll);
            }
        }

        return res;
    }

    /**
     * List<Map> 行列转换
     * row1: col11, col12, col13
     * row2: col21, col22, col23
     * row3: col31, col32, col33
     * row4: col41, col42, col43
     * ->
     * col11, col21, col31, col41
     * col12, col22, col32, col42
     * col13, col23, col33, col43
     */
    @SuppressWarnings("unused")
    public static List<Map<String, Object>> turnListMap(List<Map<String, Object>> list) {

        List<Map<String, Object>> res = new ArrayList<>();

        if (list != null && list.size() > 0) {
            Set<?> set = list.get(0).keySet();
            int colSize = set.size();
            int rowSize = list.size();
            int cc = 0;
            for (Object key : set) {
//				Tools.out(key);
                Map<String, Object> col = new LinkedHashMap<String, Object>();
                for (int i = 0; i < rowSize; i++) {
                    col.put("col" + i, getList(list, i, "" + key));
                }
                res.add(0, col);
                cc++;
            }

        }

        return res;
    }

    /**
     * 根据url 获取对象 map.key1.listcc[0].list[2].key3
     *
     * @param map
     * @param urls
     * @return null/obj
     */
    @SuppressWarnings("rawtypes")
    public static <T> T getMapUrl(Map map, String urls) {
        return getMapUrl(map, urls, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T getMapUrl(Map map, String urls, T defaultValue) {
        T res = defaultValue;
        Object obj = map;
        Object temp = null;
        String toUrl = ""; //实际路径
        String itemCopy = "";

        if (urls.length() <= 0) {    //非查询root
            res = (T) map;
        } else {
            String[] arr = urls.split("\\."); // map.list[0].map.aaa   map.list
            int cc = -1;
            for (int i = 0; i < arr.length; i++) {
                String item = arr[i];
                itemCopy = item;
                //list[0] -> list 0
                cc = -1;
                if (item.charAt(item.length() - 1) == ']') { //数组
                    item = item.substring(0, item.length() - 1); //去除]
                    item = item.replace('[', ' ');
                    String[] ss = item.split(" ");
                    if (ss.length >= 2) {
                        item = ss[0];
                        cc = Integer.valueOf(ss[1]);
                    }
                }
                if (obj instanceof List) { //数组 list 不出现该情况
                    break;
                } else if (obj instanceof Map) {//最后查询层级应该是此
                    Map objMap = (Map) obj;
                    temp = objMap.get(item); //预读取取出值为 map list ? 否则中断跳出
                    if (temp == null) break;
                    if (temp instanceof Map) {    //取出对象为map
                        obj = temp;
                    } else if (temp instanceof List) { //输出对象为list
                        List tempList = (List) temp;
                        if (cc >= 0 && cc < tempList.size()) { //list[2]
                            obj = tempList.get(cc);
                        } else { //list
                            obj = temp;
                        }
                    } else { //基本类型
                        obj = temp;
                    }
                } else { //已经是基本类型则 不再继续子层级查询 理应不存在访问此
                    break;
                }
                toUrl += itemCopy + ".";
            }
            if (toUrl.length() > 0)
                toUrl = toUrl.substring(0, toUrl.length() - 1);
            if (toUrl.equals(urls)) {
                res = (T) obj;
            }
        }
        return res;
    }

    /**
     * 按照url添加
     * set map1.map11.cc test
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String putMapUrl(Map map, String urls, Object value) {
        if (urls.length() == 0) return null;
        String key = "";
        String[] keys = urls.split("\\."); //map1,   map1 map11
        key = keys[0];
        Object make = value;
        for (int i = keys.length - 1; i >= 0; i--) {
            urls = urls.substring(0, urls.length() - Math.min(urls.length(), (keys[i].length() + 1))); //map1.map11 -> map1 map11-value
            if (i == 0) {//
                map.put(keys[i], make);
                break;
            } else {
                Object temp = getMapUrl(map, urls);
                if (temp == null) {
                    make = new Bean().put(keys[i], make); //map{map11:value}
                } else { //必须为map
                    if (temp instanceof Map)
                        ((Map) temp).put(keys[i], make);
                    else//找到上层url 替换为新map
                        putMapUrl(map, urls, value);
                    break;
                }
            }
        }
        return key;
    }


    @SuppressWarnings("rawtypes")
    public static List<Map> testList() {
        List<Map> res = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            res.add(
                    map()
                            .put("id", i)
                            .put("name", "name-" + i)
                            .build()
            );
        }

        return res;
    }

    public static Map<String, String> testSSMap() {
        Map<String, String> res = new LinkedHashMap<>();
        for (int i = 0; i < 3; i++) {
            res.put("id", "id" + i);
            res.put("name", "name" + i);
        }
        return res;
    }

    public static Map<String, Object> testSOMap() {
        Map<String, Object> res = new LinkedHashMap<>();
        for (int i = 0; i < 3; i++) {
            res.put("id", "id" + i);
            res.put("name", "name" + i);
        }
        return res;
    }

    public static Map<String, Double> testSDMap() {
        Map<String, Double> res = new LinkedHashMap<>();
        for (int i = 0; i < 3; i++) {
            res.put("id", i * 1.0);
            res.put("name", i * 2.0);
        }
        return res;
    }

}