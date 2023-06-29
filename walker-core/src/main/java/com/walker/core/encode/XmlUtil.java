package com.walker.core.encode;

import com.walker.core.mode.Bean;
import com.walker.core.mode.BeanLinked;
import com.walker.core.util.FileUtil;
import com.walker.core.util.Tools;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * xml工具类
 */
public class XmlUtil {
	protected static Logger log = LoggerFactory.getLogger(XmlUtil.class);

	/**
	 * 解析某个节点 不包含root本身
	 *
	 * @param level   深度
	 * @param element 节点
	 * @return 该节点的属性 子节点列表
	 */
	@SuppressWarnings("unchecked")
	public static Object turnElement(int level, Element element) {
		String fill = Tools.fillStringBy("", " ", level * 4, 0);
		level++;
		Object res = null;

		//获取 简单属性 节点的属性键值
		List<Attribute> attrs = element.attributes();
		List<Element> elements = element.elements();

		if (attrs.size() > 0 || elements.size() > 0) {
			BeanLinked value = new BeanLinked();

			for (Attribute attr : attrs) {
				debug(fill + "**attr " + attr.getName() + " : " + attr.getValue());
				value.put(attr.getName(), attr.getValue());
			}

			Map<String, List<Element>> index = new LinkedHashMap<String, List<Element>>();
			for (Element item : elements) {
				String key = item.getName();
				List<Element> list = index.get(key);
				if (list == null) {
					list = new ArrayList<>();
					index.put(key, list);
				}
				list.add(item);
			}

			for (Entry<String, List<Element>> item : index.entrySet()) {
				String key = item.getKey();
				List<Element> list = item.getValue();
				if (list != null) {
					if (list.size() == 1) {
						debug(fill + "##node map " + key);
						value.put(key, turnElement(level, list.get(0)));
					} else {
						debug(fill + "##node list " + key + " " + list.size());
						List<Object> arr = new ArrayList<>();
						for (Element ele : list) {
							arr.add(turnElement(level, ele));
						}
						value.put(key, arr);
					}
				}
				res = value;
			}
		} else {
			res = element.getTextTrim();
			debug(fill + "##node str " + res);
		}


		//获取 对象属性 子节点列表
		//简单类型 字符串
		//item:1
		//<item>1</item>
		//
		//对象转换
		//item:{
		//		name: aaa
		//		value: bbb
		//}
		//<item name="aaa" value="bbb" />
		//<item>
		//	<name>aaa</name>
		//	<value>bbb</value>
		//</item>
		//
		//数组转换
		//item:[
		//	aaa,
		//	bbb
		//]
		//<item>a</item
		//<item>b</item>

		return res;
	}

	public static String turnElement(Object obj) {
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		sb.append(turnElement(0, obj, ""));
		return sb.toString();
	}

	public static String turnElement(int level, Object obj, Object parentKey) {
		level = level < 0 ? 0 : level;
		String fill = Tools.fillStringBy("", " ", level * 8, 0);
		String fillNext = Tools.fillStringBy("", " ", (level + 1) * 8, 0);

		level++;
		StringBuilder sb = new StringBuilder();
		if (obj instanceof Map) {
			sb.append("\n");
			Map<?, ?> map = (Map<?, ?>) obj;
			sb.append(fill).append("<").append(parentKey).append(">");
			sb.append("\n");
			for (Object key : map.keySet()) {
				Object value = map.get(key);
				sb.append(fillNext).append(turnElement(level, value, key)).append("\n");
			}
			sb.append(fill).append("</").append(parentKey).append(">");
		} else if (obj instanceof List) {
			sb.append("\n");
			List<?> list = (List<?>) obj;
			for (Object item : list) {
				sb.append(fill).append(turnElement(level, item, parentKey)).append("\n");
			}
		} else {//		<key>xxxx</key>\n
			sb
					.append("<").append(parentKey).append(">")
					.append(obj)
					.append("<").append(parentKey).append(">")
			;
		}
		String res = sb.toString();
		if (StringUtils.isBlank(res)) {
			throw new RuntimeException("res is null ? ");
		}
		return res.replace("\n\n\n", "\n\n");
	}

	private static void debug(Object... objects) {
		log.debug(Arrays.toString(objects));
	}

	/**
	 * 解析某节点为完整的map 包含root
	 *
	 * @param element
	 */
	public static Object parseFile(Element element) {
//		return new BeanLinked().set(element.getName(), turnElement(0, element));
		return turnElement(0, element);
	}

	public static Object parseFile(File file) throws DocumentException {
		// 创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		// 通过reader对象的read方法加载books.xml文件,获取docuemnt对象。
		Document document = reader.read(file);
		// 通过document对象获取根节点bookstore
		Element element = document.getRootElement();
		return parseFile(element);
	}

	public static void saveConfig(Map<?, ?> map, File file) {
		FileUtil.saveAs(turnElement(map), file, false);
	}

	/**
	 * 解析文件
	 *
	 * @param filePath
	 * @throws DocumentException
	 */
	public static Object parseFile(String filePath) throws DocumentException {
		return parseFile(new File(filePath));
	}

	public static void saveConfig(Map<?, ?> map, String filePath) throws DocumentException {
		saveConfig(map, new File(filePath));
	}

	public static void main(String[] args) throws DocumentException {
		Object bean = parseFile("xmlTest.xml");
//    	debug(JsonUtil.makeJson(bean, 0));
		debug(JsonUtil.makeJson(bean, 6));


		String path = ClassLoader.getSystemResource("").getPath() + "plugin.json";
		String str = FileUtil.readByLines(path, null, "utf-8");
		log.warn("plugin mgr init file: " + path);
		log.warn(str);

		Bean bb = JsonUtil.get(str);
		String s = turnElement(bb);
		debug(s);

	}

}
