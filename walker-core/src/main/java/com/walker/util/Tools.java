package com.walker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Tools {

	private static final int toolong = 600;
	private static final Logger log = LoggerFactory.getLogger("Tools");

	public static void main(String[] args) {
		out(replace("aaa${bbb}, ${bbb}, ${ccc}", "bbb", "hello"));
	}

	/**
	 * @param num:要获取二进制值的数      64 + 15 -> 0100 1111
	 * @param index:倒数第一位为0，依次类推 低位到高位
	 */
	public static int get(int num, int index) {
		return (num & (0x1 << index)) >> index;
	}

	/**
	 * 从10进制转换到toN进制
	 * list 0 最低位
	 *
	 * @param toN
	 * @param num
	 */
	public static List<Integer> binary(int toN, int num, int resSize, int defaultValue) {
		int fromN = 10;
		List<Integer> res = new ArrayList<>();
		while (num > 0) {
			int last = num % toN;
			res.add(last);

			num = num / toN;
		}
		while (res.size() < resSize) {
			res.add(defaultValue);
		}
		return res;
	}

	public static void regex(String[] str, String regex) {
		//编译正则
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
		//使用正则匹配
		java.util.regex.Matcher matcher = pattern.matcher("");

		//matcher.reset(); //重置匹配位置
		for (String item : str) {
			matcher.reset(item); //新匹配str
			while (matcher.find()) {
				out(matcher.group());
			}

		}
	}

	/**
	 * 异常栈格式化
	 */
	public static String toString(Throwable e) {
		if (e == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			e.printStackTrace(pw);
			return " \nException:" + e.getMessage() + " \n" + sw;
//            return sw.toString();
		} finally {
			pw.close();
		}
	}

	public static String fillInt(Object obj, int len) {
		return fillStringBy(obj + "", " ", len, 1);
	}

	public static String tooLongCut(String str) {
		if (str.length() > toolong)
			return "len." + str.length() + " size."
					+ Tools.calcSize(str.length()) + str.substring(0, toolong);
		return str;
	}

	public static String cutString(String str, int len) {
		if (str != null && str.length() > len) {
			str = str.substring(0, len);
		}
		return str;
	}

	/**
	 * 通过字符串长度，计算大概的 流量大小 MB KB B char=B
	 */
	public static String calcSize(int filesize) {
		return calcSize((long) filesize);
	}

	/**
	 * 通过字符串长度，计算大概的 流量大小 MB KB B char=B
	 */
	public static String calcSize(long filesize) {
		return filesize > 1024 * 1024 * 1024 ? (float) (10 * filesize / (1024 * 1024 * 1024))
				/ 10 + "G"
				: (filesize > 1024 * 1024 ? filesize / 1024 / 1024 + "M"
				: filesize / 1024 + "K");
	}

	/**
	 * ms计算耗时 10M8S100ms
	 */
	public static String calcTime(long timemill) {
		return timemill > 60 * 1000 ? (float) (10 * timemill / (60 * 1000))
				/ 10 + "M " + timemill % (60 * 1000) / 1000 + "S "
				: (timemill > 1000 ? timemill / 1000 + "S " + timemill % 1000
				+ "Ms " : timemill + "Ms ");

//		return DurationFormatUtils.formatDuration(timemill, "HH:mm:ss.SSS");
	}

	public static String getValueEncoded(String value) {
		if (value == null)
			return "null";
		String newValue = value.replace("\n", "");
		try {
			return URLEncoder.encode(newValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String out(String str) {
		if (log == null || log.getClass().isInstance(NOPLogger.class) || System.getProperty("path_conf") == null) {
			System.out.println(TimeUtil.getTimeHms() + "." + Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + "." + str);
		} else {
			log.info(str);
		}
		return str;
	}

	public static String out(Object object) {
		return out(String.valueOf(object));
	}

	public static String out(Object... objects) {
//		out(Arrays.toString(objects));
		return out(objects2string(objects));
	}

	public static String out(Logger log, Object... objects) {
		String str = objects2string(objects);
		log.info(str);
		return str;
	}

	public static <T> void formatOut(T[] list) {
		int i = 0;
		for (T obj : list) {
			out(i++, obj);
		}
	}

	public static <T> void formatOut(Collection<T> list) {
		int i = 0;
		for (T obj : list) {
			out(i++, obj);
		}
	}

	public static String strings2string(String[] strs) {
		StringBuilder res = new StringBuilder("[ ");
		for (String str : strs) {
			res.append(str + ", ");
		}
		if (strs != null && strs.length > 0) {
			res.substring(0, res.length() - 2);
		}
		res.append(" ]");

		return res.toString();
	}

	public static String objects2string(Object... objects) {
		if (objects.length == 1) {
			return String.valueOf(objects[0]);
		}
		String[] res = objects2strings(objects);
//		return strings2string(res);
//		return Arrays.toString(res);
		String ress = "[";
		for (String str : res) {
			ress += "," + str;
		}
		ress += "]";
		return ress;
	}

	//传入数组 作为动态参数 则也会 变为动参 除非手动上转为Object
	public static String[] objects2strings(Object... objects) {
//		return  ; //无法解决 序列动态参数为 数组的情况
		if (objects == null)
			return new String[]{""};
		String[] objs = new String[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				if (objects[i] instanceof Object[]) {
					objs[i] = objects2string((Object[]) objects[i]);
				} else {
					objs[i] = LangUtil.toString(objects[i]);
				}
			} else
				objs[i] = "null!";
		}
		return objs;
	}

	/**
	 * 都不为null && ” “
	 */
	public static boolean notNull(Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null || objects[i].toString().equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 都为null || ” “
	 */
	public static boolean isNull(Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null && !objects[i].toString().equals("")) {
				return false;
			}
		}
		return true;
	}


	/**
	 * str x i
	 */
	public static String getFill(int i, String str) {
		String res = "";
		for (int j = 0; j < i; j++) {
			res += str;
		}
		return res;
	}

	/**
	 * (12, " ", 6, 0)
	 * priorOrNext 1标识 在后面填充  0表示前面留空
	 */
	public static String fillStringBy(String str, String by, int tolen, int priorOrNext) {
		if (str.length() > tolen) {
			str = str.substring(0, tolen);
		} else {
			StringBuilder sb = new StringBuilder();
			int fromlen = str.length();
			for (int i = 0; i < (tolen - fromlen) / by.length(); i++) {
				sb.append(by);
			}
			if (priorOrNext == 0) {
				sb.reverse();
				str = sb + str;
			} else {
				str = str + sb;
			}
		}
		return str;
	}

	/**
	 * 获取随机数，从到，补齐位数
	 */
	public static String getRandomNum(int fromNum, int toNum, int num) {
		int ii = (int) (Math.random() * (toNum - fromNum) + fromNum);
		String res = "" + ii;
		for (int i = res.length(); i < num; i++) {
			res = "0" + res;
		}
		return res;
	}

	/**
	 * 获取数组 索引
	 */
	public static <TYPE> int indexOf(TYPE[] array, TYPE str) {
		if (array == null)
			return -1;
		if (str == null)
			return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null && str == null) {
				return i;
			} else if (array[i] != null && array[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 数字提取
	 * 0-21-23 [133]  [^(\\d)]+ -> 3232
	 */
	public static String filterNum(String num) {
		return num.replaceAll("-+|\\s+|\\[+|\\]+|L$|D$|F$|l$|d$|f$", "");
	}

	/**
	 * 颜色16进制转换
	 * 2455， 342， 32 -> #21ff00
	 */
	public static String color2string(int r, int g, int b) {
		String res = Tools.fillStringBy(Integer.toHexString(r) + "", "0", 2, 0)
				+ Tools.fillStringBy(Integer.toHexString(g) + "", "0", 2, 0)
				+ Tools.fillStringBy(Integer.toHexString(b) + "", "0", 2, 0);
		return res;
	}

	/**
	 * linux变量替换
	 */
    public static String replace(String buf, String key, String value) {
		return buf.replaceAll("\\$\\{" + key + "\\}", value);
    }
}
