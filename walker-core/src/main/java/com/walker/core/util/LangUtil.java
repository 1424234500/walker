package com.walker.core.util;

import com.walker.core.exception.ErrorException;
import org.apache.http.client.utils.CloneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 对象类型转换工具
 */
public class LangUtil {
	private static final Logger log = LoggerFactory.getLogger("Lang");
	/**
	 * 下面代码用于将36位的UUID字符串转为22位的字符串，提升系统运行效率
	 */
	private static final char[] CHAR_MAP;

	static {
		CHAR_MAP = new char[64];
		for (int i = 0; i < 10; i++) {
			CHAR_MAP[i] = (char) ('0' + i);
		}
		for (int i = 10; i < 36; i++) {
			CHAR_MAP[i] = (char) ('a' + i - 10);
		}
		for (int i = 36; i < 62; i++) {
			CHAR_MAP[i] = (char) ('A' + i - 36);
		}
		CHAR_MAP[62] = '_';
		CHAR_MAP[63] = '-';
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static byte[] int2bytes(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (value >> (24 - i * 8));
		}
		return b;
	}

	public static int bytes2int(byte[] b) {
		return (((int) b[0] << 24) + (((int) b[1]) << 16) + (((int) b[2]) << 8) + b[3]);
	}

	/**
	 * 解析判断Object类型 回调处理 适用于递归处理的 JSON XML 格式化
	 * <p>
	 * Map
	 * key1-value
	 * key2-value
	 * <p>
	 * List
	 * arr[0]
	 * arr[1]
	 * <p>
	 * Java simple Object
	 * id-value
	 * name-value
	 * <p>
	 * String
	 * Int
	 * Long
	 * String
	 */
	public static void onObject(Object obj, Call call) {
		if (call == null) return;

		if (obj instanceof Map) {
			call.onMap((Map<?, ?>) obj);
		} else if (obj instanceof List) {
			call.onList((List<?>) obj);
		} else if (
				obj instanceof String
						|| obj instanceof Integer
						|| obj instanceof Double
						|| obj instanceof Long
						|| obj instanceof Float
						|| obj instanceof Character
						|| obj instanceof Short
						|| obj instanceof Boolean
		) {
			call.onString(String.valueOf(obj));
		} else {//按照基本类型 key-value转化
			if (obj != null) {
				Map<String, Object> map = LangUtil.turnObj2Map(obj);
				call.onMap(map);
			} else {
				call.onString("null");
			}
		}

	}

	public static String toString(Object obj) {
		if (obj instanceof Map) {
			return obj.toString();
		} else if (obj instanceof Collection) {
			Collection<Object> list = (Collection<Object>) obj;
			StringBuilder sb = new StringBuilder("list.").append(list.size()).append(" [ ");
			int i = 0;
			for (Object item : list) {
				sb.append(" ").append(i++).append(".").append(toString(item)).append(",");
			}
			sb.setLength(sb.length() - 1);
			sb.append("]");
			return sb.toString();
		} else if (
				obj instanceof String
						|| obj instanceof Integer
						|| obj instanceof Double
						|| obj instanceof Long
						|| obj instanceof Float
						|| obj instanceof Character
						|| obj instanceof Short
						|| obj instanceof Boolean
		) {
			return String.valueOf(obj);
		} else {//按照基本类型 key-value转化
			if (obj != null) {
				Map<String, Object> map = LangUtil.turnObj2Map(obj);
				return toString(map);
			}
		}
		return "null!";
	}

	public static List<Map<String, Object>> turnObj2MapList(List<?> obj) {
		List<Map<String, Object>> res = new ArrayList<>();
		if (obj != null)
			for (Object item : obj) {
				res.add(turnObj2Map(item));
			}
		return res;
	}

	public static <T> List<T> turnMap2ObjList(List<Map<String, Object>> map, Class<T> clz) {
		List<T> res = new ArrayList<>();
		if (map != null)
			for (Map<String, Object> item : map) {
				res.add(turnMap2Obj(item, clz));
			}
		return res;
	}

	/**
	 * class bean对象  fieds 属性值 转换为map
	 *
	 * @param obj
	 * @return {name:xxx, agx:18}
	 */
	public static Map<String, Object> turnObj2Map(Object obj) {
		Map<String, Object> map = new HashMap<>();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field item : fields) {
			String key = item.getName();
			Object value = null;
			item.setAccessible(true);
			try {
				value = item.get(obj);
				map.put(key, value);
			} catch (IllegalArgumentException | IllegalAccessException e) { // 私有不计算策略
				e.printStackTrace();
			}
		}
		log.debug("turn obj to " + map.keySet() + " from " + obj);
		return map;
	}

	/**
	 * class bean对象  method 调用值 转换为map
	 *
	 * @param obj
	 * @param regex eg: is*
	 * @return {isReadable: true, isHelp:false}
	 */
	public static Map<String, Object> turnObj2Map(Object obj, String startWith) {
		Map<String, Object> map = new HashMap<>();
		for(Method item : obj.getClass().getMethods()){
			String key = item.getName();
			Object value = null;
			try {
				if(
						(startWith == null || startWith.length() == 0 || key.startsWith(startWith))
						&& item.getParameters().length <= 0 //无参数
				) {
					value = item.invoke(obj);
					map.put(key, value);
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				log.error(e.getMessage(), e);
			}
		}
		log.debug("turn obj to " + map.keySet() + " from " + obj);
		return map;
	}
	/**
	 * class bean对象转换为map
	 *
	 * @param map
	 */
	public static <T> T turnMap2Obj(Map<String, Object> map, Class<T> clz) {
		T res = null;

		if (res instanceof Map) {
			return (T) map;
		}
		try {
			res = clz.newInstance();

			Field[] fields = clz.getDeclaredFields();
			for (Field item : fields) {
				String key = item.getName();
				Object value = map.get(key);
				item.setAccessible(true);
				try {
					item.set(res, value);
				} catch (IllegalArgumentException | IllegalAccessException e) { // 私有不计算策略
					log.error(map + " " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			log.error(map.toString() + " " + e.getMessage(), e);
		}
		log.debug("turn map to " + res + " from " + map.keySet());

		return res;
	}

	/**
	 * 目标类型转换
	 *
	 * @param obj
	 */
	public static <T> T turn(Object obj) {
		return turn(obj, null);
	}

	/**
	 * 目标类型转换
	 *
	 * @param obj
	 * @param defaultValue
	 */
	@SuppressWarnings("unchecked")
	public static <T> T turn(Object obj, T defaultValue) {
		T res = null;
		if (obj == null) {
			res = defaultValue;
		} else {
			if (defaultValue instanceof Integer) {
				res = (T) (to(obj, (Integer) defaultValue));
			} else if (defaultValue instanceof Long) {
				res = (T) (to(obj, (Long) defaultValue));
			} else if (defaultValue instanceof Double) {
				res = (T) (to(obj, (Double) defaultValue));
			} else if (defaultValue instanceof Boolean) {
				res = (T) (to(obj, (Boolean) defaultValue));
			} else if (defaultValue instanceof String) {
				res = (T) (String.valueOf(obj));
			} else {
				res = (T) obj;
			}
		}
		return res;
	}

	/**
	 * 唯一id生成
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		uuid = hexTo64(uuid);
		return uuid;
	}

	/**
	 * 获取时序keyid
	 *
	 * @param key
	 * @return 20190102-key-uuid
	 */
	public static String getTimeSeqId(String key) {
		String time = TimeUtil.getTimeSequence();
		String str = getUUID();
		return time + "_" + key + "_" + str.substring(0, Math.min(4, str.length()));
	}

	/**
	 * 时序递增id生成2
	 *
	 * @return 20181012_jaxjkxj
	 */
	public static String getTimeSeqId() {
		String time = TimeUtil.getTimeSequence();
		String str = getUUID();
		return time + "_" + str.substring(0, Math.min(6, str.length()));
	}

	/**
	 * 时序递增id生成
	 *
	 * @return 39939384884_jaxjkxj
	 */
	public static String getGenerateId() {
		long time = System.nanoTime();
		String str = getUUID();
		return time + "_" + str.substring(0, Math.min(6, str.length()));
	}

	/**
	 * 将16进制字符串转换为64进制
	 *
	 * @param hex 16进制字符串
	 * @return 64进制字符串
	 */
	private static String hexTo64(String hex) {
		StringBuilder r = new StringBuilder();
		int index = 0;
		final int size = 3;
		int[] buff = new int[size];
		int l = hex.length();
		for (int i = 0; i < l; i++) {
			index = i % size;
			buff[index] = Integer.parseInt("" + hex.charAt(i), 16);
			if (index == 2) {
				r.append(CHAR_MAP[buff[0] << 2 | buff[1] >>> 2]);
				r.append(CHAR_MAP[(buff[1] & size) << 4 | buff[2]]);
			}
		}
		return r.toString();
	}

	/**
	 * 转型为整型
	 *
	 * @param obj 原始对象
	 * @param def 缺省值
	 * @return 整型
	 */
	public static Integer to(Object obj, Integer def) {
		if (obj != null) {
			if (obj instanceof Integer) {
				return (Integer) obj;
			} else if (obj instanceof Number) {
				return ((Number) obj).intValue();
			} else if (obj instanceof Boolean) {
				return (Boolean) obj ? 1 : 0;
			} else if (obj instanceof Date) {
				return (int) ((Date) obj).getTime();
			} else {
				try {
					return Integer.parseInt(obj.toString());
				} catch (Exception e) {
					try {
						return (new Double(obj.toString()).intValue());
					} catch (Exception e2) {
						return def;
					}
				}
			}
		} else {
			return def;
		}
	}

	/**
	 * 转型为长整型
	 *
	 * @param obj 原始对象
	 * @param def 缺省值
	 * @return 长整型
	 */
	public static Long to(Object obj, Long def) {
		if (obj != null) {
			if (obj instanceof Long) {
				return (Long) obj;
			} else if (obj instanceof Number) {
				return ((Number) obj).longValue();
			} else if (obj instanceof Boolean) {
				return (Boolean) obj ? 1L : 0L;
			} else if (obj instanceof Date) {
				return ((Date) obj).getTime() * 1L;
			} else {
				try {
					return Long.parseLong(obj.toString());
				} catch (Exception e) {
					try {
						return (new Double(obj.toString())).longValue();
					} catch (Exception e2) {
						return def;
					}
				}
			}
		} else {
			return def;
		}
	}

	/**
	 * 转型为双精度浮点型
	 *
	 * @param obj 原始对象
	 * @param def 缺省值
	 * @return 双精度浮点型
	 */
	public static Double to(Object obj, Double def) {
		if (obj != null) {
			if (obj instanceof Double) {
				return (Double) obj;
			} else if (obj instanceof Float) {
				return ((Float) obj).doubleValue();
			} else if (obj instanceof Number) {
				return ((Number) obj).doubleValue();
			} else if (obj instanceof Boolean) {
				return (Boolean) obj ? 1d : 0d;
			} else if (obj instanceof Date) {
				return ((Date) obj).getTime() * 1.0;
			} else {
				try {
					return new Double(obj.toString());
				} catch (Exception e) {
					return def;
				}
			}
		} else {
			return def;
		}
	}

	/**
	 * 转型为布尔值
	 *
	 * @param obj 原始对象
	 * @param def 缺省值
	 * @return 布尔值
	 */
	public static Boolean to(Object obj, Boolean def) {
		if (obj != null) {
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			} else if (obj instanceof Integer) {
				return ((Integer) obj).intValue() == 1;
			} else if (obj instanceof Long) {
				return ((Long) obj).longValue() == 1;
			} else if (obj instanceof Double) {
				return ((Double) obj).doubleValue() == 1;
			} else if (obj instanceof Date) {
				return ((Date) obj).getTime() == 1;
			} else {
				String str = obj.toString().toUpperCase();
				return str.equalsIgnoreCase("TRUE") || str.equalsIgnoreCase("YES") || str.equals("1");
			}
		} else {
			return def;
		}
	}

	public static <T> T cloneObject(final T obj) {
		try {
			return CloneUtils.cloneObject(obj);
		} catch (CloneNotSupportedException e) {
			throw new ErrorException(e);
		}
	}

	public interface Call {
		void onMap(Map<?, ?> map);

		void onList(List<?> list);

		void onString(String str);
	}
}
