package com.walker.spring.intercept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 限流
 */
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(getClass());


    /**
     * 在渲染视图之后被调用；
     * 可以用来释放资源
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object method, Exception e) throws Exception {

    }

    /**
     * 该方法在目标方法调用之后，渲染视图之前被调用；
     * 可以对请求域中的属性或视图做出修改
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object method, ModelAndView modelAndView) throws Exception {
        // log.info("==============执行顺序: 2、postHandle================");
    }

    /**
     * 可以考虑作权限，日志，事务等等
     * 该方法在目标方法调用之前被调用；
     * 若返回TURE,则继续调用后续的拦截器和目标方法
     * 若返回FALSE,则不会调用后续的拦截器和目标方法
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object method) throws Exception {

        return true;
    }

}  