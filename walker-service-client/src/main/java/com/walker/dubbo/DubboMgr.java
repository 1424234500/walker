package com.walker.dubbo;


import com.alibaba.dubbo.rpc.RpcContext;
import com.walker.core.aop.ConnectorAdapter;;
import com.walker.service.EchoService;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 每个节点根据配置 同时是提供者也是消费者 可配置化
 *
 * dubbo环境初始化  配置文件加载   上下文提供
 *
 */
public class DubboMgr extends ConnectorAdapter; {
    private static Logger log = Logger.getLogger("dubbo");

    private String path = "dubbo-service-config.xml";
    private ClassPathXmlApplicationContext context;

    private DubboMgr() {

    }
    private static class SingletonFactory{
        private static DubboMgr instance;
        static {
            instance = new DubboMgr();
        }

    }
    public static DubboMgr getInstance() {
        return SingletonFactory.instance;
    }
    public boolean doTest() {
        if( DubboMgr.getInstance() == null){
            log.error("dubbo is not started!!!");
            return false;
        }else{
            log.info(("dubbo is started "));
            EchoService echoService = DubboMgr.getService("echoService");
            log.info(echoService.echo("hello!"));
            return true;
        }
    }



    public DubboMgr setDubboXml(String path){
        this.path = path;
        log.info("dubbo xml : " + path);
        return this;
    }
    public void start(){
        if(context == null || !context.isActive()) {
            log.warn("dubbo singleton instance construct " + SingletonFactory.class + " path:" + path);
            context = new ClassPathXmlApplicationContext(path.split(","));
            context.start();


            Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run(){
                    log.warn("dubbo shutdownhook");
                    context.stop();
                }
            });

            check();

        }else{
            log.warn("have started " + context.toString());
        }
    }



    public static <T> T getService(String serviceName){
        //rpc cookie?
        String cookie = RpcContext.getContext().getAttachment("index"); // 隐式传参，后面的远程调用都会隐式将这些参数发送到服务器端，类似cookie，用于框架集成，不建议常规业务使用
        T service = (T)getContext().getBean(serviceName);
        log.info("serviceName:" + serviceName + " cookie:" + cookie + " service:" + service);
        return service;
    }
    public static <T> T getService(Class<T> clz){
        String name = clz.getSimpleName();
        return getService(name);
    }



    public static ClassPathXmlApplicationContext getContext(){
        DubboMgr dubboMgr = getInstance();
        if(dubboMgr.context == null){
            dubboMgr.start();
        }
        return dubboMgr.context;
    }


}
