package com.walker.core.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http工具类
 * <p>
 * 模拟浏览器header encode 编码 解码 可为null 默认HTTP配置编码 userAgent 身份 默认浏览器标示 connect time
 * 超时
 * <p>
 * <p>
 * 支持restful接口访问 get post set delete
 * <p>
 * 文件下载url
 * <p>
 * <p>
 * 4xx状态码表示客户端错误，主要有下面几种。 400 Bad Request：服务器不理解客户端的请求，未做任何处理。 401
 * Unauthorized：用户未提供身份验证凭据，或者没有通过身份验证。 403 Forbidden：用户通过了身份验证，但是不具有访问资源所需的权限。
 * 404 Not Found：所请求的资源不存在，或不可用。 405 Method Not Allowed：用户已经通过身份验证，但是所用的 HTTP
 * 方法不在他的权限之内。 410 Gone：所请求的资源已从这个地址转移，不再可用。 415 Unsupported Media
 * Type：客户端要求的返回格式不支持。比如，API 只能返回 JSON 格式，但是客户端要求返回 XML 格式。 422 Unprocessable
 * Entity ：客户端上传的附件无法处理，导致请求失败。 429 Too Many Requests：客户端的请求次数超过限额。 500 Internal
 * Server Error：客户端请求有效，服务器处理时发生了意外。 503 Service
 * Unavailable：服务器无法处理请求，一般用于网站维护状态。
 */
public class HttpUtil {
	//	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36
	private final static String DEFAULT_BROWSER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
	/**
	 * 打印结果长度
	 */
	private static final int SHOW_STRING_LEN = 100;
	/**
	 * httpClient连接池 用于长期持有 上下文相关 session
	 */
	private static final Map<String, HttpClient> mapClient;
	public static int timeout_mill_request = 3000;
	public static int timeout_mill_connect = 3000;
	public static int timeout_mill_socket = 6000;
	protected static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	static {
		mapClient = new HashMap<String, HttpClient>();
	}

	public static RequestConfig makeTimeoutConfig(int requestTimeout, int connectTimeout, int socketTimeout, HttpHost proxy) {
		RequestConfig config = null;
		if (requestTimeout > 0 || connectTimeout > 0 || socketTimeout > 0) {
			RequestConfig.
//			Builder builder = RequestConfig.custom();
					Builder builder = RequestConfig.copy(RequestConfig.DEFAULT);
			builder.setProxy(proxy);
			if (requestTimeout > 0) {
				builder.setConnectionRequestTimeout(requestTimeout); // 从连接池中获取连接的超时时间
			}
			if (connectTimeout > 0) {
				builder.setConnectTimeout(connectTimeout); // 与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
			}
			if (socketTimeout > 0) {
				builder.setSocketTimeout(socketTimeout); // socket读数据超时时间：从服务器获取响应数据的超时时间
			}
			config = builder.build();
		}
		return config;
	}

	/**
	 * 参数编码
	 *
	 * @param str
	 * @throws UnsupportedEncodingException
	 */
	public static String makeEncode(String str, String encode) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, encode).replaceAll("\\+", "%20");
	}

	/**
	 * 拼接url get
	 *
	 * @param url
	 * @param data
	 * @param encode
	 * @throws UnsupportedEncodingException
	 */
	public static String makeUrl(String url, Map<?, ?> data, String encode) throws UnsupportedEncodingException {
		if (data == null || data.size() <= 0)
			return url;
		if (!url.contains("?"))
			url = url + "?";
		String dd = makeUrlData(data, encode);
		url = url + (url.endsWith("?") ? "" : "&") + dd;
		return url;
	}

	public static String makeUrlData(Map<?, ?> data, String encode) throws UnsupportedEncodingException {
		StringBuilder ddd = new StringBuilder();
		for (Object key : data.keySet()) {
			ddd.append("&").append(key).append("=").append(makeEncode(String.valueOf(data.get(key)), encode));
		}
		String dd = ddd.length() > 0 ? ddd.substring(1, ddd.length()) : "";
		return dd;
	}

	/**
	 * 设置header集合
	 *
	 * @param httpMessage
	 * @param headers
	 */
	public static void makeHeader(HttpMessage httpMessage, Map<?, ?> headers) {
//		httpMessage.addHeader("Connection", "keep-alive");
		httpMessage.addHeader("User-Agent", DEFAULT_BROWSER);
		if (headers != null && headers.size() > 0) {
			for (Entry<?, ?> item : headers.entrySet()) {
				httpMessage.addHeader(String.valueOf(item.getKey()), String.valueOf(item.getValue()));
			}
		}
	}

	/**
	 * 创建新的浏览器端httpClient
	 */
	private static HttpClient makeHttpClient() {
//创造HttpClient浏览器端
//		3.x
//		HttpClient client = newHttpClient();
//		client.setConnectionTimeout(30000);
//		client.setTimeout(30000);
//
//		4.x
//		HttpClient httpClient=newDefaultHttpClient();
//		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,2000);//连接时间
//		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,2000);//数据传输时间
//
//		4.3
		RequestConfig config = makeTimeoutConfig(HttpUtil.timeout_mill_request, HttpUtil.timeout_mill_connect, HttpUtil.timeout_mill_socket, null);
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultRequestConfig(config);
		HttpClient client = builder.build();
		return client;
	}

	/**
	 * 获取一个浏览器端httpClient 单例 或者 连接池控制
	 *
	 * @param name null or '' 则每次返回一个新浏览器 否则缓存返回历史的同名浏览器
	 */
	public static HttpClient getClient(String name) {
		HttpClient client = null;
		if (name == null || name.length() == 0) {
			client = makeHttpClient();
		} else if (mapClient.containsKey(name)) {
			client = mapClient.get(name);
		} else {
			client = makeHttpClient();
			mapClient.put(name, client); // 加入缓存
		}
		return client;
	}

	/**
	 * 获取新的浏览器httpClient
	 */
	public static HttpClient getClient() {
		return getClient("");
	}

	public static String doPost(String url, Object data, String encode, Map<?, ?> headers) throws Exception {
		String str = JSON.toJSONString(data);
		HttpPost request = new HttpPost();
		request.setEntity(new StringEntity(str, encode));
		return executeString("", request, url, encode, encode, headers, null);
	}

	public static String doPut(String url, Object data, String encode, Map<?, ?> headers) throws Exception {
		String str = JSON.toJSONString(data);
		HttpPut request = new HttpPut();
		request.setEntity(new StringEntity(str, encode));
		return executeString("", request, url, encode, encode, headers, null);
	}

	public static String doGet(String url, Map<?, ?> data, String encode, Map<?, ?> headers)
			throws Exception {
		url = makeUrl(url, data, encode);
		HttpGet request = new HttpGet();
		return executeString("", request, url, encode, encode, headers, null);
	}

	public static String doDelete(String url, Map<?, ?> data, String encode, Map<?, ?> headers)
			throws Exception {
		url = makeUrl(url, data, encode);
		HttpDelete request = new HttpDelete();
		return executeString("", request, url, encode, encode, headers, null);
	}


	public static String executeString(String who, HttpRequestBase request, String url, String encode, String decode, Map<?, ?> headers, RequestConfig timeout)
			throws Exception {
		Watch w = new Watch();
		String res = "";
		try {
			w.put("http " + request.getMethod() + " " + url + " " + request.getProtocolVersion());
			w.put("who", who);
			w.put("encode", encode);
			w.put("decode", decode);
			w.put(" \nheaders", request.getAllHeaders());
			w.put(" \nRequestConfig:");
			if (timeout == null) {
				w.put("requestTimeout=" + HttpUtil.timeout_mill_request + ",connectTimeout=" + HttpUtil.timeout_mill_connect + ",socketTimeout=" + HttpUtil.timeout_mill_socket);
			} else {
				w.put(timeout);
			}
			HttpResponse response = executeResponse(who, request, url, headers, timeout);
			w.cost(" \nexecute");

			StatusLine status = response.getStatusLine();
			w.put(" \ncode", status);
			HttpEntity entity = response.getEntity();
			res = parseHttpEntity(entity, decode);

			w.cost(" \nparseString");
			w.res(res.substring(0, Math.min(res.length(), SHOW_STRING_LEN)), log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {

		}
		return res;
	}

	/**
	 * 根据请求类型 url 头 超时配置获取response
	 */
	public static File executeFile(String who, HttpRequestBase request, String url, Map<?, ?> headers, RequestConfig timeout, File file)
			throws URISyntaxException, IOException {
		Watch w = new Watch();
		try {
			w.put("http " + request.getMethod() + " " + url + " " + request.getProtocolVersion());
			w.put("who", who);
			w.put(" \nheaders", request.getAllHeaders());
			w.put(" \nRequestConfig:");
			if (timeout == null) {
				w.put("requestTimeout=" + HttpUtil.timeout_mill_request + ",connectTimeout=" + HttpUtil.timeout_mill_connect + ",socketTimeout=" + HttpUtil.timeout_mill_socket);
			} else {
				w.put(timeout);
			}
			HttpResponse response = executeResponse(who, request, url, headers, timeout);
			w.cost(" \nexecute");

			StatusLine status = response.getStatusLine();
			w.put(" \ncode", status);
			HttpEntity entity = response.getEntity();

			file = parseHttpEntity(entity, file);

			w.cost(" \nparseFile");
			w.res(file.getAbsolutePath(), log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {

		}
		return file;
	}

	/**
	 * 根据请求类型 url 头 超时配置获取response
	 */
	public static HttpResponse executeResponse(String who, HttpRequestBase request, String url, Map<?, ?> headers, RequestConfig timeout)
			throws URISyntaxException, IOException {
		request.setURI(new URI(url));
		// 设置超时 依赖配置默认值
		if (timeout != null)
			request.setConfig(timeout);
		// 设置请求头
		makeHeader(request, headers);
		HttpClient client = getClient(who);
		return client.execute(request);
	}

	/**
	 * 解析response实体
	 *
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static String parseHttpEntity(HttpEntity entity, String decode) throws IllegalStateException, IOException {
		String res = "";
		ByteArrayOutputStream os = new ByteArrayOutputStream(FileUtil.SIZE_BUFFER);
		if (entity != null) {
			// getResponse
			InputStream is = null;
			try {
				is = entity.getContent();
				FileUtil.copyStream(is, os);
				if (decode != null && decode.length() > 0)
					res = os.toString(decode);
				else
					res = os.toString();
			} finally {
				if (os != null) {
					os.close();
				}
				if (is != null)
					is.close();
			}
		}
		return res;
	}

	/**
	 * 解析response实体 file
	 *
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public static File parseHttpEntity(HttpEntity entity, File file) throws IllegalStateException, IOException {
		String res = "";
		FileOutputStream os = new FileOutputStream(file);
		if (entity != null) {
			// getResponse
			InputStream is = null;
			try {
				is = entity.getContent();
				FileUtil.copyStream(is, os);
				res = os.toString();
			} finally {
				if (os != null) {
					os.close();
				}
				if (is != null)
					is.close();
			}
		}
		return file;
	}

	/**
	 * 下载文件
	 *
	 * @param url
	 * @param saveFilePath
	 * @throws IOException
	 */
	public static void download(String url, String saveFilePath) throws Exception {
		File file = new File(saveFilePath);
		if (file.isDirectory()) {
			throw new Exception("save to dir?");
		} else {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		}
		download(url, file);
	}

	/**
	 * 下载文件 统计耗时
	 *
	 * @param url
	 * @param file
	 */
	public static void download(String url, File file) throws Exception {
		Watch w = new Watch("http download " + url + " " + file.getAbsolutePath());
		log.info(w.toString());
		long length = 0;

		InputStream is = null;
		try {
			URL ur = new URL(url);
			is = ur.openStream();
			length = FileUtil.saveFile(is, file, false);
			w.put("size", Tools.calcSize(length));
			w.res(null, log);
		} catch (IOException e) {
			w.exceptionWithThrow(e, log);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public static HttpEntity getFormEntity(Map<?, ?> data, String encode) {
		List<BasicNameValuePair> list = new ArrayList<>();
		for (Entry<?, ?> entry : data.entrySet()) {
			list.add(new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
		}
		return new UrlEncodedFormEntity(list, Charset.forName(encode));

	}
}
