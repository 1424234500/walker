package com.walker.core.encode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.walker.mode.Bean;
import com.walker.mode.BeanLinked;
import com.walker.util.Tools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * org.json.tools
 */


public class JsonUtil {
	private static class TestMain {
		public static void main(String[] argv) {
			Bean bean = new Bean().set("key1", 23).set("key2", 232);
			Tools.out(makeJson(bean));
			List<Object> list = new ArrayList<>();
			list.add("asdf");
			list.add(bean);
			Tools.out(makeJson(list));
		}
	}

	public static String makeJson(Object obj) {
		return makeJson(obj, 0);
	}

	public static String makeJson(Object obj, int indentFactor) {
		return JsonFastUtil.toString(obj);
	}

	/**
	 * list转json
	 *
	 * @param list
	 */
	public static String makeJson(Collection<?> list) {
		return JsonFastUtil.toString(list);
	}

	/**
	 * 递归 解析JSONArray为list
	 */
	private static List<?> toList(JSONArray ja) {
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < ja.size(); i++) {
			Object object = ja.get(i);
			if (object instanceof JSONArray) {
				JSONArray jaa = (JSONArray) object;
				list.add(toList(jaa));
			} else if (object instanceof JSONObject) {
				JSONObject joo = (JSONObject) object;
				list.add(toMap(joo));
			} else { // 普通 类型 int string double ...
				list.add(object);
			}
		}
		return list;
	}

	/**
	 * 递归 解析JSONObject为map
	 */
	private static BeanLinked toMap(JSONObject jo) {
		BeanLinked map = new BeanLinked();
		for (Object key : jo.keySet()) {
			Object object = jo.get(key);
			if (object instanceof JSONArray) {
				JSONArray jaa = (JSONArray) object;
				map.put(key, toList(jaa));
			} else if (object instanceof JSONObject) {
				JSONObject joo = (JSONObject) object;
				map.put(key, toMap(joo));
			} else { // 普通 类型 int string double ...
				map.put(key, object);
			}
		}
		return map;
	}

	/**
	 * 解析json为map/list/string
	 *
	 * @param jsonstr
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String jsonstr) {
		try {
			int type = getType(jsonstr);
			if (type == 0) {
				return (T) jsonstr;
			}
			if (type == 1) {
				JSONObject jo = JSON.parseObject(jsonstr);
				return (T) toMap(jo);
			} else if (type == 2) {
				JSONArray ja = JSON.parseArray(jsonstr);
				return (T) toList(ja);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 0字符串 1map 2list
	 */
	public static int getType(String jsonstr) {
		int res = 0;
		if (jsonstr != null) {
			String str = StringUtils.strip(jsonstr);
			if (str.length() > 0) {
				if (str.charAt(0) == '{' && str.charAt(str.length() - 1) == '}') {
					res = 1;
				} else if (str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']') {
					res = 2;
				}
			}
		}
		return res;
	}

	public static void out(String str) {

	}
}
