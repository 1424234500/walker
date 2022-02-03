package com.walker.config;

import com.walker.mode.Page;
import com.walker.mode.User;
import com.walker.mode.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 环境上下文
 */
@Component
public class Context {
    private final static String STR_REQUEST = "_REQUEST_";
    private final static String STR_RESPONSE = "_RESPONSE_";
    @SuppressWarnings("unused")
    private final static String STR_TABLENAME = "_TABLENAME_";
    private final static String STR_TIME = "_TIME_";
    /**
     * 线程级别上下文
     */
    private static final ThreadLocal<Bean> threadLocal = new ThreadLocal<Bean>() {
        @Override
        protected Bean initialValue() {
            super.initialValue();
            return new Bean();
        }
    };
    private final static String STR_TOKEN = "_TOKEN_";
    private final static String STR_USER = "_USER_";
    private final static String STR_INFO = "_INFO_";

    public static String getRedisKeyUserPush() {
        return "push:user:";
    }

    public static String getRedisKeyDeviceUser() {
        return "push:device";
    }

    /**
     * 获取线程 专用 键值对map
     */
    private static Bean getContext() {
        return Context.threadLocal.get();
    }

    /**
     * clear
     */
    public static void clear() {
        Context.threadLocal.remove();
    }

    /**
     * 获取分页对象
     * @return
     */

    /**
     * 获取上下文存储的键值对
     *
     * @param key
     * @param defaultValue
     */
    public static <T> T get(Object key, T defaultValue) {
        return Context.getContext().get(key, defaultValue);
    }

    /**
     * 获取上下文存储的键值对
     *
     * @param key
     */
    public static Object get(Object key) {
        return Context.getContext().get(key);
    }

    /**
     * 设置上下文存储的键值对
     *
     * @param key
     * @param value
     */
    public static <T> Bean set(Object key, T value) {
        return Context.getContext().set(key, value);
    }

    /**
     * 设置request
     *
     * @param request
     */
    public static Bean setRequest(HttpServletRequest request) {
        return Context.set(STR_REQUEST, request);
    }

    public static HttpServletRequest getRequest() {
        Bean bean = Context.getContext();
        if (bean.containsKey(STR_REQUEST)) {
            return (HttpServletRequest) (bean.get(STR_REQUEST));
        }
        return null;
    }

    public static Bean setResponse(HttpServletResponse response) {
        return Context.set(STR_RESPONSE, response);
    }

    public static HttpServletResponse getResponse() {
        Bean bean = Context.getContext();
        if (bean.containsKey(STR_RESPONSE)) {
            return (HttpServletResponse) (bean.get(STR_RESPONSE));
        }
        return null;
    }

    /**
     * 记录处理时间
     */
    public static Bean setTimeStart() {
        return Context.set(STR_TIME, System.currentTimeMillis());
    }

    /**
     * 获取处理时间
     */
    public static long getTimeStart() {
        return Context.get(STR_TIME, System.currentTimeMillis());
    }

    public static String getToken() {
        return Context.get(STR_TOKEN, "");
    }

    /**
     * 设置当前用户token
     *
     * @param token
     */
    public static void setToken(String token) {
        Context.set(STR_TOKEN, token);
    }

    public static User getUser() {
        return Context.get(STR_USER, null);
    }

    /**
     * 设置当前用户
     */
    public static void setUser(User user) {
        Context.set(STR_USER, user);
    }

    public static String getInfo() {
        return Context.get(STR_INFO, "");
    }

    /**
     * 设置info
     */
    public static void setInfo(String info) {
        Context.set(STR_INFO, info);
    }

}