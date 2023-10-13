package com.walker.spring.config;


import com.walker.service.Config;
import com.walker.spring.intercept.LogInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 配置mvc web.xml spring-mvc.xml
 * <p>
 * 过滤器
 * 过滤器是什么?
 * 简单的来说，过滤器就是过滤的作用，在web开发中过滤一些我们指定的url。
 * 过滤器主要做什么？
 * 过滤掉一些不需要的东西，例如一些错误的请求。
 * 也可以修改请求和相应的内容。
 * <p>
 * 过滤器的代码实现
 * 过滤器(filter)有三个方法，其中初始化（init）和摧毁（destroy）方法一般不会用到，主要用到的是doFilter这个方法。
 * 而至于怎么过滤呢？
 * 如果过滤通过，则在doFilter执行filterChain.doFilter(request,response);该方法。
 * <p>
 * 拦截器
 * <p>
 * 拦截器是什么?
 * 简单的来说，就是一道阀门，拦截不需要的东西。
 * 拦截器主要做什么？
 * 对正在运行的流程进行干预。
 * <p>
 * 拦截器的代码实现。
 * 拦截器也主要有三个方法，其中
 * preHandle是在请求之前就进行调用，如果该请求需要被拦截，则返回false，否则true;
 * postHandle是在请求之后进行调用，无返回值;
 * afterCompletion是在请求结束的时候进行调用，无返回值。
 * <p>
 * 监听器
 * 监听项目启动
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    //    @Bean(name = "multipartResolver")
//    public MultipartResolver multipartResolver(){
//        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//        resolver.setDefaultEncoding("UTF-8");
//        resolver.setResolveLazily(true); //resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
//        resolver.setMaxInMemorySize(1024 * 1024);
//        resolver.setMaxUploadSize(100 * 1024 * 1024);//上传文件大小 5M 5*1024*1024
//        return resolver;
//    }
    static final List<String> exceptStatic = Arrays.asList("/webjars/**",
            "/static/**",
            "/html/*");
    //    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//    }
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info(Config.getPre() + "WebConfig addInterceptors");
//
////        token检测 设置用户 环境上下文    未登录则跳转登录 拦截?
//        registry.addInterceptor(new UserInterceptors() )
//                .addPathPatterns("/**")
//                .excludePathPatterns(Arrays.asList(
//                        "/shiro/**",
//                        "/webjars/**",
//                        "/static/**",
//                        "/html/*",
//                        "/*",
//                        "/swagger-resources/**",
//                        "/v2/**"
//
//                ));
//        url ip  参数 耗时统计监控

        //配置 拦截 /list结尾的请求
        //    /*表示只拦截 /这一层目录下的/list   比如 拦截/dept/list  不会拦截/api/dept/list
        //    /** 表示拦截  /这一层目录下的包含子目录的/list 比如拦截 /api/dept/list
        registry.addInterceptor(new LogInterceptors())
                .addPathPatterns("/**")
                .excludePathPatterns(Arrays.asList(
                        "/webjars/**",
                        "/static/**",
                        "/html/*",
                        "/*",
                        "/swagger-resources/**",
                        "/v2/**"

                ));
//        registry.addInterceptor(new UserInterceptors())
//            .excludePathPatterns(
//                Arrays.asList(new String[]{
//                        "/*/*onlogin.do",
//                        "/*/*loginin.do"
//                })
//            )
//        ;


//        <!-- 拦截器已采用url过滤.do模式只拦截controller，此处无效？ -->
//	<!-- 装配拦截器  实现aop 事务 日志 权限 限流 -->
//    <mvc:interceptors>
//  		<!-- 日志拦截访问url 外层bean拦截所有 -->
//     	<bean class="com.walker.spring.event.intercept.LogInterceptors" />
//     	<!-- 登录拦截例外跳转 -->
//    	<mvc:interceptor>
// 	 	   <mvc:mapping path="/*/**" />
//	 	   <!-- 资源拦截例外 -->
//           <!--  <mvc:exclude-mapping path="/**/*.css"/> -->
//           <mvc:exclude-mapping path="/include/**"/>
//   	 	   <mvc:exclude-mapping path="/*/**.ajax" />
//           <!-- 登录拦截例外 -->
//	 	   <mvc:exclude-mapping path="/*/*onlogin.do" />
//	 	   <mvc:exclude-mapping path="/*/*loginin.do" />
//	       <bean class="com.walker.spring.event.intercept.UserInterceptors" />
//      	</mvc:interceptor>
//    </mvc:interceptors>
    }
//
//    @Bean
//    public ServletListenerRegistrationBean listenerRegist() {
//        log.info("ServletListenerRegistrationBean");
//        ServletListenerRegistrationBean srb = new ServletListenerRegistrationBean();
//        srb.setListener(new ContextListener());
//        return srb;
//    }
//
//
//
//
//
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info("configureMessageConverters init");
//
//        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
//        List<MediaType> mediaTypes = new ArrayList<>();
//        mediaTypes.add(MediaType.TEXT_XML);
//        mediaTypes.add(MediaType.APPLICATION_XML);
//        XStreamMarshaller xStreamMarshaller=new XStreamMarshaller();
//        marshallingHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
//        marshallingHttpMessageConverter.setMarshaller(xStreamMarshaller);
//        marshallingHttpMessageConverter.setUnmarshaller(xStreamMarshaller);
//        converters.add(marshallingHttpMessageConverter);
//    }

//    //异常处理
//    @Bean
//    public ExceptionHandler exceptionResolver(){
//        ExceptionHandler exceptionHandler = new ExceptionHandler();
//        return exceptionHandler;
//    }


}
