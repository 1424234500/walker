package com.walker.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 常见需求 对象属性连接处理
 */
public class ListConnectUtil {
	private static final Logger log = Logger.getLogger(ListConnectUtil.class);

	public static void main(String[] argv) {
		test1();
		test2();
	}

	public static void test1() {
		List<Map<String, Object>> list1 = new ArrayList<>();
		List<Map<String, Object>> list2 = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("id", i);
			map1.put("one", i);
			list1.add(map1);

			Map<String, Object> map2 = new HashMap<>();
			map2.put("id", i);
			map2.put("double", i * i);
			list2.add(map2);
		}

		List<Object> list1Ids = list1.stream().map(item -> item.get("id")).collect(Collectors.toList());
		List<Map<String, Object>> listQueryByIds = list2;

		Map<Object, Map<String, Object>> mapQueryByIds = listQueryByIds.stream().collect(Collectors.toMap(item -> item.get("id"), Function.identity(), (key1, key2) -> key2));

		list1.forEach(item -> {
			Map<String, Object> queryObj = mapQueryByIds.get(item.get(("id")));
			if (queryObj != null) {
				item.put("two", queryObj.get("double"));
			}
		});
		System.out.println(JSON.toJSONString(list1, SerializerFeature.PrettyFormat));
		System.out.println("-----------");
	}

	public static void test2() {
		List<Map<String, Object>> list1 = new ArrayList<>();
		List<Map<String, Object>> list2 = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			Map<String, Object> map1 = new HashMap<>();
			map1.put("id", i);
			map1.put("one", i);
			list1.add(map1);

			Map<String, Object> map2 = new HashMap<>();
			map2.put("id", i);
			map2.put("double", i * i);
			list2.add(map2);
		}
		connectBy(list1, new IProperty<Map<String, Object>, Object, Map<String, Object>>() {
			@Override
			public Object getKey(Map<String, Object> itemObj) {
				return itemObj.get("id");
			}

			@Override
			public List<Map<String, Object>> getQueryResByKeys(List<Object> listIds) {
				return list2;
			}

			@Override
			public Object getKeyQuery(Map<String, Object> itemObj) {
				return itemObj.get("id");
			}

			@Override
			public void setProperty(int listIndex, Object o, Map<String, Object> itemObj, List<Map<String, Object>> stringObjectMap) {
				itemObj.put("twww", stringObjectMap);
			}
		});

		System.out.println(JSON.toJSONString(list1, SerializerFeature.PrettyFormat));
		System.out.println("-----------");
	}

	/**
	 * @param list        原list
	 * @param iProperty   相关接口实现 取键 查询 设置键
	 * @param <OBJ>       原list对象类型
	 * @param <KEY>       原list对象的关联键类型  连接条件
	 * @param <QUERY_OBJ> 附加查询结果的对象类型
	 */
	public static <OBJ, KEY, QUERY_OBJ> List<OBJ> connectBy(List<OBJ> list, IProperty<OBJ, KEY, QUERY_OBJ> iProperty) {
		StringBuilder info = new StringBuilder().append("connectFrom size:" + (list == null ? "error null" : list.size()));
		try {
//		获取ids
//		List<KEY> listIds = list.stream().map(item -> iProperty.getKey(item)).collect(Collectors.toList());
			Set<KEY> listIds = new LinkedHashSet<>(); //去重
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					KEY key = iProperty.getKey(list.get(i));
					if (key == null) {
						info.append("\n warn i." + i + ".null");
					} else {
						info.append("\n i." + i + "." + key);
						listIds.add(key);
					}
				}
			}
			if (listIds.size() <= 0) {
				info.append("\n error get ids 0 ");
			} else {
//		查询paas获取映射结果
				List<QUERY_OBJ> listPaas = iProperty.getQueryResByKeys(new ArrayList<>(listIds));//new ArrayList<>();
				info.append("\nconnectTo   size:" + (listPaas == null ? "error null" : listPaas.size()));
				Map<KEY, List<QUERY_OBJ>> mapPaas = new LinkedHashMap<>();
				if (listPaas != null && listPaas.size() > 0) {
					for (int i = 0; i < listPaas.size(); i++) {
						KEY key = iProperty.getKeyQuery(listPaas.get(i));
						if (key == null) {
							info.append("\n warn iq." + i + ".null");
						} else {
//							listIds.addChildToNode(key);
							List<QUERY_OBJ> ilist = mapPaas.get(key);
							if (ilist == null) {
								ilist = new ArrayList<>();
								mapPaas.put(key, ilist);
							}
							ilist.add(listPaas.get(i));
							info.append("\n iq." + i + "." + key + "." + ilist.size());
						}
					}
				}
				if (mapPaas.size() <= 0) {
					info.append("\n error get paas ids 0 ");
				} else {
					for (int i = 0; i < list.size(); i++) {
						OBJ obj = list.get(i);
						KEY key = iProperty.getKey(obj);   //原对象的key去新对象map中查找 ==
						List<QUERY_OBJ> queryObj = mapPaas.get(key);
						if (queryObj != null) {
//						关联设置属性
							iProperty.setProperty(i, key, obj, queryObj);
						} else {
							info.append("\n warn " + i + "." + key + ".no connect");
						}
					}
				}
			}
		} catch (Exception e) {
			info.append("\n error " + e.getMessage());
			log.error(info, e);
		} finally {
			if (info.indexOf("error") > 0) {
				log.error(info);
			} else if (info.indexOf("warn") > 0) {
				log.warn(info);
			} else {
				log.info(info);
			}
			System.out.println(info);
		}
		return list;
	}

	/**
	 * 常见需求
	 * 1.过车记录，附加 图片列表，连接查询，连接条件
	 * 2.other
	 */
	public interface IProperty<OBJ, KEY, QUERY_OBJ> {
		/**
		 * 获取旧对象的连接值
		 */
		KEY getKey(OBJ itemObj);

		/**
		 * 根据旧对象连接值s查询新对象集合
		 */
		List<QUERY_OBJ> getQueryResByKeys(List<KEY> listIds);

		/**
		 * 获取新对象连接值
		 */
		KEY getKeyQuery(QUERY_OBJ itemObj);

		/**
		 * 设置旧对象关于新对象 关于连接值 连接源序号
		 */
		void setProperty(int listIndex, KEY key, OBJ itemObj, List<QUERY_OBJ> queryObj);
	}


}

