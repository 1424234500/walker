package com.walker.intercept;

import com.walker.config.ShiroConfig;
import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.core.mode.school.User;
import com.walker.service.LogService;
import com.walker.service.LoginService;
import com.walker.spring.config.Context;
import com.walker.spring.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器 日志 登录/访问权限 事务   监控所有用户操作和登录并记录日志数据库
 * 拦截器是在上下文容器 Spring Context 初始化之前执行，所以没有办法直接在拦截器中注入Service对象
 *
 * 解决方案1：
 *  ShiroConfig shiroConfig = SpringContextUtil.getBean("shiroConfig")
 * 解决方案2：
 * 	springboot 代码注入interceptor @Bean依赖自动装配
 *
 * @author Walker
 *
 */
public class UserInterceptors implements HandlerInterceptor{
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Cache<String> cache = ConfigMgr.getInstance();
	@Autowired
	private LoginService loginService;
	@Autowired
	private LogService logService;
	@Autowired
	private ShiroConfig shiroConfig;

    /**
     * 在渲染视图之后被调用； 
     * 可以用来释放资源 
     */   
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object method, Exception e)    throws Exception {  
    	// log.info("==============执行顺序: 3、afterCompletion================");
    }  
    /** 
     * 该方法在目标方法调用之后，渲染视图之前被调用； 
     * 可以对请求域中的属性或视图做出修改 
     *  
     */  
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object method, ModelAndView modelAndView) throws Exception {  
    	// log.info("==============执行顺序: 2、postHandle================");
    }  

    /** 
     * 可以考虑作权限，日志，事务等等 
     * 该方法在目标方法调用之前被调用； 
     * 若返回TURE,则继续调用后续的拦截器和目标方法 
     * 若返回FALSE,则不会调用后续的拦截器和目标方法 
     *  
     */  
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object method) throws Exception {  
       // log.info("==============执行顺序: 1、preHandle================");

		Object tokenObj = request.getSession().getAttribute("TOKEN");
//		request.getHeader("TOKEN")
		String token = tokenObj == null ? "" : String.valueOf(tokenObj);
		if(token.length() == 0){
			token = request.getHeader("TOKEN");
			token = token == null ? "" : token;
			log.debug("session is null, then userAgent token " + token);
		}
		if(token.length() == 0){
			token = request.getParameter("TOKEN");
			token = token == null ? "" : token;
			log.debug("header userAgent is null, then parameter token " + token);
		}
//	    Map<String, Object> map =  cache.get(LoginService.CACHE_KEY, new HashMap<>());
//		Map<String, String> user = redisDao.hmGet(token);
		User user = token.length() > 0 ? shiroConfig.getOnlineUser(token) : null;

		if( user != null ){
			log.debug(user + " token:" + tokenObj + " go ");
			shiroConfig.keeponUser(token);
			Context.setToken(token);
			Context.setUser(user);
	    }else{
			String requestUri = request.getRequestURI();
			String contextPath = request.getContextPath();
			String url = requestUri.substring(contextPath.length());  //[/student/listm]
			log.info("ipport:" + request.getRemoteAddr() + ":" + request.getRemotePort() + " " + url + " token:" + tokenObj + " go login 401");
			SpringUtil.echoErr(response,  HttpServletResponse.SC_UNAUTHORIZED, token);
//			SpringUtil.echo401(response, token);
//			SpringUtil.sendRedirect(response, "/");
	    	return false;
	    }

        return true;  
    }  
  
}  