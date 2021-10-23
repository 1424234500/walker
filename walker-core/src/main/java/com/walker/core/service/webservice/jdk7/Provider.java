package com.walker.core.service.webservice.jdk7;

import com.walker.core.aop.Call;
import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import com.walker.core.service.webservice.jdk7.client.ServiceClass.ServiceClassImpl;
import com.walker.core.service.webservice.jdk7.client.ServiceClass.ServiceClassImplService;
import com.walker.util.ClassUtil;
import com.walker.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * webService 服务初始化
 * <p>
 * <p>
 * a,在需要暴露的实现impl类上添加@WebService注解 （1.6  javax.jws.WebService） 类中所有非静态方法都会被发布； 静态方法和final方法不能被发布；方法上加@WebMentod(exclude=true)后，此方法不被发布
 * b,EndPoint(端点服务)发布 WebService 专门用于发布服务 javax.xml.ws.Endpoint）
 * <p>
 * c,客户端根据java工具导入wsdl src目录下 设定包名 影响package import语句 生成访问控制工具
 * wsimport -s . -p util.service.webservice.client.ServiceHelloWebservice http://localhost:8089/ServiceHelloWebservice?wsdl
 * d,修改xxxxService.java中的 static区域的 url改变动态化地址ip
 * e,调用
 */
public class Provider implements Call {
    private static final Logger log = LoggerFactory.getLogger(Provider.class);
    private static final Map<String, Object> map;

    static {
        map = new HashMap<>();
    }

    public static String getUrl() {
        if (map.size() > 0) {
            return map.keySet().iterator().next();
        }
        return "";
    }

    /**
     * 初始化服务
     * 验证测试
     */
    @Override
    public void call() {
        log.info("** 初始化 WebService provider ---------------------- ");
        Cache<String> cache = ConfigMgr.getInstance();
        int port = cache.get("port_webservice", 8090);
        String clzs = cache.get("on_list_service", "");
        String[] clzss = clzs.split(",");

        for (int i = 0; i < clzss.length; i++) {
            try {
                Object obj = ClassUtil.newInstance(clzss[i]);
                String name = obj.getClass().getSimpleName();
                if (name.endsWith("Impl")) {
                    name = name.substring(0, name.length() - "Impl".length());
                }
                String url = "http://localhost:" + port + "/" + name;
                try {
//					Endpoint.publish(url, obj);
                    map.put(url, obj);
                    log.info("###publish.ok." + i + " " + url);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("###publish.error." + i + " " + url + " " + e);
                }
            } catch (Exception e) {

            }
        }

        log.info("**! 初始化完毕 WebService provider------------------- ");

        log.info("-- 开始测试WebService --------------");
        try {
//			if(map.size() > 0)
//				log.info(HttpUtil.get(map.keySet().iterator().next()));
            ServiceClassImplService webService = new ServiceClassImplService();
            ServiceClassImpl service = webService.getServiceClassImplPort();
            Tools.out(service.test("hello webservice"));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("测试web service error !" + e);
        }

        log.info("--! 测试完毕 ------------------- ");
    }


}
