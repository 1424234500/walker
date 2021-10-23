package com.walker.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.annotation.ResponseJSONP;
import com.walker.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@ControllerAdvice
public class ControllerConfig implements ResponseBodyAdvice<Object> {
    public static final String KEY = "ResponseBody";
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    /**
     * 拦截所有controller返回值
     * 全局 ajax？ package？
     * ResponseBody
     *
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        log.debug("beforeBodyWrite controller config");

        /**
         * 此处request response非传统？
         * response值转换
         */
        ServletServerHttpResponse responseTemp = (ServletServerHttpResponse) serverHttpResponse;
        HttpServletResponse response = responseTemp.getServletResponse();
        ServletServerHttpRequest requestTemp = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest request = requestTemp.getServletRequest();
        String res = "";
        if (o instanceof ResponseJSONP) {
            res = JSON.toJSONString(o);
        } else if (o instanceof Response) {
            res = JSON.toJSONString(o);
        }
        request.setAttribute("ResponseBody", res);

        return o;
    }
}
