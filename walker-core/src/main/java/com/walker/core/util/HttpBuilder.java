package com.walker.core.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.util.Map;

/**
 * builder模式构建动态设置的http各种请求
 *
 * @author walker
 */
public class HttpBuilder {
    private Type type;
    /**
     * 浏览器 新访问?
     */
    private String who;
    private String url;
    /**
     * 表单参数 否则 json参数
     */
    private Boolean form = false;
    private Map<?, ?> data;
    private String encode = "utf-8";
    private String decode;
    private Map<?, ?> headers;

    private int requestTimeout = 0;
    private int connectTimeout = 0;
    private int socketTimeout = 0;

    private HttpRequestBase request;
    private RequestConfig timeout;

    public HttpBuilder(String url, Type type) {
        setUrl(url);
        setType(type);
    }

    public Boolean getForm() {
        return form;
    }

    public HttpBuilder setForm(Boolean form) {
        this.form = form;
        return this;
    }

    public Type getType() {
        return type;
    }

    public HttpBuilder setType(Type type) {
        this.type = type;
        return this;
    }

    public String getWho() {
        return who;
    }

    public HttpBuilder setWho(String who) {
        this.who = who;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public HttpBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<?, ?> getData() {
        return data;
    }

    public HttpBuilder setData(Map<?, ?> data) {
        this.data = data;
        return this;
    }

    public String getEncode() {
        return encode;
    }

    public HttpBuilder setEncode(String encode) {
        this.encode = encode;
        return this;
    }

    public String getDecode() {
        return encode;
    }

    public HttpBuilder setDecode(String decode) {
        this.decode = decode;
        return this;
    }

    public Map<?, ?> getHeaders() {
        return headers;
    }

    public HttpBuilder setHeaders(Map<?, ?> headers) {
        this.headers = headers;
        return this;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public HttpBuilder setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public HttpBuilder setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public HttpBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public String buildString() throws Exception {
        makeData();
        return HttpUtil.executeString(who, request, url, encode, decode, headers, timeout);
    }

    public HttpResponse buildResponse() throws Exception {
        makeData();
        return HttpUtil.executeResponse(who, request, url, headers, timeout);
    }

    public File buildFile(File file) throws Exception {
        if (file.exists() && file.isFile()) {
            file.delete();
        } else {
            file.createNewFile();
        }
        makeData();
        return HttpUtil.executeFile(who, request, url, headers, timeout, file);
    }

    private void makeData() throws Exception {
        timeout = HttpUtil.makeTimeoutConfig(requestTimeout, connectTimeout, socketTimeout);

        switch (this.type) {
            case DELETE:
                if (data != null && data.size() > 0)
                    url = HttpUtil.makeUrl(url, data, encode);
                request = new HttpDelete();
                break;
            case PUT:
                HttpPut put = new HttpPut();
                if (data != null && data.size() > 0) {
                    if (this.form) {
                        put.setEntity(HttpUtil.getFormEntity(data, encode));
                    } else {
                        put.setEntity(new StringEntity(JSON.toJSONString(data), encode));
                    }
                }
                request = put;
                break;
            case POST:
                HttpPost post = new HttpPost();
                if (data != null && data.size() > 0) {
                    if (this.form) {
                        post.setEntity(HttpUtil.getFormEntity(data, encode));
                    } else {
                        post.setEntity(new StringEntity(JSON.toJSONString(data), encode));
                    }
                }
                request = post;
                break;
            case GET:
            default:
                if (data != null && data.size() > 0)
                    url = HttpUtil.makeUrl(url, data, encode);
                request = new HttpGet();
        }

        if (decode == null || decode.length() == 0) {
            decode = encode;
        }

    }


    public enum Type {
        PUT, GET, POST, DELETE,
    }

}
