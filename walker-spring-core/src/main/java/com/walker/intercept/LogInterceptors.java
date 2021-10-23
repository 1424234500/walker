package com.walker.intercept;

import com.walker.config.Context;
import com.walker.config.ControllerConfig;
import com.walker.mode.LogModel;
import com.walker.mode.User;
import com.walker.service.Config;
import com.walker.service.LogService;
import com.walker.system.Pc;
import com.walker.util.RequestUtil;
import com.walker.util.SpringContextUtil;
import com.walker.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 拦截器 Interceptor 不依赖 Servlet 容器，依赖 Spring 等 Web 框架，
 * 在 SpringMVC 框架中是配置在SpringMVC 的配置文件中，在 SpringBoot 项目中也可以采用注解的形式实现。
 * 拦截器是 AOP 的一种应用，底层采用 Java 的反射机制来实现的。
 * 与过滤器一个很大的区别是在拦截器中可以注入 Spring 的 Bean，能够获取到各种需要的 Service 来处理业务逻辑，而过滤器则不行。
 * <p>
 * 过滤器英文叫 Filter，是 JavaEE 的标准，依赖于 Servlet 容器，使用的时候是配置在 web.xml 文件中的，可以配置多个，执行的顺序是根据配置顺序从上到下。常用来配置请求编码以及过滤一些非法参数，垃圾信息或者是网站登录验证码。
 * <p>
 * 拦截器 日志 登录/访问权限 事务  监控所有访问地址和参数打印
 * 实现应用之性能监控
 * 拦截器是实现成单例的，因此不管用户请求多少次都只访问同一个拦截器实现，即线程不安全。
 * 解决方案是：使用ThreadLocal，它是线程绑定的变量，提供线程局部变量（一个线程一个ThreadLocal，
 * A线程的ThreadLocal只能看到A线程的ThreadLocal，不能看到B线程的ThreadLocal）。
 *
 * @author Walker
 */
public class LogInterceptors implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(getClass());
    // 统计应用性能
    private final NamedThreadLocal startTimeThreadLocal = new NamedThreadLocal("ThreadLocal-Action-Stop-Start-Time");
    //    @Autowired
//	  @Qualifier("logService")
//    LogService logService;
    //此处不能自动注入? 扫描注入包配置问题
    LogService logService = SpringContextUtil.getBean("logService");

    /**
     * 在渲染视图之后被调用；
     * 可以用来释放资源
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object method, Exception e) throws Exception {
        // log.info("==============执行顺序: 3、afterCompletion================");
        //结束时间   统计应用的性能
        long endTime = System.currentTimeMillis();
        // 得到线程绑定的局部变量（开始时间）
        long beginTime = (long) startTimeThreadLocal.get();
        long time = endTime - beginTime;
        String info = Context.getInfo() + "[cost " + time + "]" + (e == null ? "" : Tools.toString(e));

        // 此处认为处理时间超过500毫秒的请求为慢请求
        if (time > 3000) {
            log.error(info);
        } else if (time > 1000) {
            log.warn(info);
        } else {
            log.info(info);
        }
        User user = Context.getUser();
        //登录用户操作日志 记录 用户id,操作url权限?,用户操作ip/mac/端口
        String id = user == null ? "" : user.getID();
        int status = response.getStatus();
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());  //[/student/listm]
        //sequenceid time userid url ip host 端口
        String ip = request.getRemoteAddr();//返回发出请求的IP地址
        String params = RequestUtil.getRequestBean(request).toString();
        String host = request.getRemoteHost();//返回发出请求的客户机的主机名
        int port = request.getRemotePort();//返回发出请求的客户机的端口号。
        String res = String.valueOf(request.getAttribute(ControllerConfig.KEY));
        res = res.length() > Config.getDbVarcharLengthLong() ? res.substring(0, Config.getDbVarcharLengthLong()) : res;
        LogModel logModel = new LogModel()
                .setUSER(id).setARGS(params)
                .setIP_PORT_FROM(ip + ":" + port).setURL(url)
                .setIP_PORT_TO(Pc.getIps().toString())
                .setCATE("controller")
                .setCOST(time)
                .setIS_EXCEPTION(e == null ? "0" : "1").setEXCEPTION(Tools.toString(e))
                .setIS_OK(status + "")
                .setRES(res);
        request.removeAttribute(ControllerConfig.KEY);

        logService.saveLogModel(logModel);

        Context.clear();
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
        //log.info("==============执行顺序: 1、preHandle================");
//    	String s = request.getCharacterEncoding();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 设置开始时间  线程绑定变量（该数据只有当前请求的线程可见）
        startTimeThreadLocal.set(System.currentTimeMillis());

        Context.setRequest(request);
        Context.setResponse(response);
        Context.setTimeStart();             //设置上下文

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());  //[/student/listm]
        String params = RequestUtil.getRequestBean(request).toString();

        String name = "", cla = "";
        if (method != null && method instanceof HandlerMethod) {
            try {
                HandlerMethod handlerMethod = (HandlerMethod) method;
                name = handlerMethod.getMethod().getName();
                cla = handlerMethod.getBean().toString();
                cla = cla.substring(0, cla.indexOf("@"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        User user = Context.getUser();
        String userid = user == null ? "null" : user.getID();
        String info = "do url:" + url + " class:" + cla + "." + name + " args:" + params + " ipport:" + request.getRemoteAddr() + ":" + request.getRemotePort() + " token:" + Context.getToken() + " user:" + userid + " ";

        Context.setInfo(info);
        //日志 记录 输出
//        log.info("++++++++ ");
        log.info(info); //输出入口日志

        return true;
    }

}  