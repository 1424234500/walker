/**
 * Copyright 2006-2015 handu.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.walker.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.walker.core.mode.sys.DubboInvoke;
import com.walker.core.util.LangUtil;
import com.walker.core.util.TimeUtil;
import com.walker.dao.DubboInvokeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MonitorService
 * <p>
 * 参考handu dubbo-monitor 修改实现监控monitor存储 自定义查询 自定义图表
 */
@Service("monitorService")
public class DubboMonitorService implements com.alibaba.dubbo.monitor.MonitorService {

    public static final String CLASSNAME = DubboMonitorService.class.getName() + ".";
    private static final String POISON_PROTOCOL = "poison";

    //    private static final String[] types = {SUCCESS, FAILURE, ELAPSED, CONCURRENT, MAX_ELAPSED, MAX_CONCURRENT};
    private static final String TIMESTAMP = "timestamp";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final boolean running = true;

    private Thread writeThread;

    private BlockingQueue<URL> queue;

    //    @Autowired
//    private RegistryContainer registryContainer;
//
//    @Autowired
//    private Dao dao;
    @Autowired
    private DubboInvokeRepository dubboInvokeRepository;

    @PostConstruct
    private void init() {
        queue = new LinkedBlockingQueue<URL>(Integer.parseInt(ConfigUtils.getProperty("dubbo.monitor.queue", "100000")));
        writeThread = new Thread(new Runnable() {
            public void run() {
                while (running) {
                    try {
                        writeToDataBase(); // 记录统计日志
                    } catch (Throwable t) { // 防御性容错
                        log.error("Unexpected error occur at write stat log, cause: " + t.getMessage(), t);
                        try {
                            Thread.sleep(5000); // 失败延迟
                        } catch (Throwable t2) {
                        }
                    }
                }
            }
        });
        writeThread.setDaemon(true);
        writeThread.setName("DubboMonitorAsyncWriteLogThread");
        writeThread.start();
    }

    /**
     * Dubbo调用信息数据写入DB
     *
     * @throws Exception
     */
    private void writeToDataBase() throws Exception {
        URL statistics = queue.take();
        if (POISON_PROTOCOL.equals(statistics.getProtocol())) {
            return;
        }
        String timestamp = statistics.getParameter(Constants.TIMESTAMP_KEY);
        Date now;
        if (timestamp == null || timestamp.length() == 0) {
            now = new Date();
        } else if (timestamp.length() == "yyyyMMddHHmmss".length()) {
            now = new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp);
        } else {
            now = new Date(Long.parseLong(timestamp));
        }
        DubboInvoke dubboInvoke = new DubboInvoke();

        dubboInvoke.setID(LangUtil.getTimeSeqId());
        try {
            if (statistics.hasParameter(PROVIDER)) {
                dubboInvoke.setTYPE(CONSUMER);
                dubboInvoke.setCONSUMER(statistics.getHost());
                dubboInvoke.setPROVIDER(statistics.getParameter(PROVIDER));
                int i = dubboInvoke.getPROVIDER().indexOf(':');
                if (i > 0) {
                    dubboInvoke.setPROVIDER(dubboInvoke.getPROVIDER().substring(0, i));
                }
            } else {
                dubboInvoke.setTYPE(PROVIDER);
                dubboInvoke.setCONSUMER(statistics.getParameter(CONSUMER));
                int i = dubboInvoke.getCONSUMER().indexOf(':');
                if (i > 0) {
                    dubboInvoke.setCONSUMER(dubboInvoke.getCONSUMER().substring(0, i));
                }
                dubboInvoke.setPROVIDER(statistics.getHost());
            }
            dubboInvoke.setINVOKE_DATE(TimeUtil.getTimeYmdHms(now.getTime()));
            dubboInvoke.setSERVICE(statistics.getServiceInterface());
            dubboInvoke.setMETHOD(statistics.getParameter(METHOD));
            dubboInvoke.setINVOKE_TIME(TimeUtil.getTimeYmdHms(statistics.getParameter(TIMESTAMP, System.currentTimeMillis())));
            dubboInvoke.setSUCCESS("" + statistics.getParameter(SUCCESS, 0));
            dubboInvoke.setFAILURE("" + statistics.getParameter(FAILURE, 0));
            dubboInvoke.setELAPSED("" + statistics.getParameter(ELAPSED, 0));
            dubboInvoke.setCONCURRENT("" + statistics.getParameter(CONCURRENT, 0));
            dubboInvoke.setMAX_ELAPSED("" + statistics.getParameter(MAX_ELAPSED, 0));
            dubboInvoke.setMAX_CONCURRENT("" + statistics.getParameter(MAX_CONCURRENT, 0));
            if (dubboInvoke.getSUCCESS().equalsIgnoreCase("0")
                    && dubboInvoke.getFAILURE().equalsIgnoreCase("0")
                    && dubboInvoke.getELAPSED().equalsIgnoreCase("0")
                    && dubboInvoke.getCONCURRENT().equalsIgnoreCase("0")
                    && dubboInvoke.getMAX_ELAPSED().equalsIgnoreCase("0")
                    && dubboInvoke.getMAX_CONCURRENT().equalsIgnoreCase("0")
            ) {
                return;
            }
            dubboInvokeRepository.save(dubboInvoke);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

//
//    /**
//     * 统计调用数据用于图表展示 使用其他自定义查询
//     *
//     * @param dubboInvoke
//     */
//    public List<DubboInvoke> countDubboInvoke(DubboInvoke dubboInvoke) {
//        if (StringUtils.isEmpty(dubboInvoke.getSERVICE())) {
//            log.error("统计查询缺少必要参数！");
//            throw new RuntimeException("统计查询缺少必要参数！");
//        }
//        return dao.getList(CLASSNAME, "countDubboInvoke", dubboInvoke);
//    }
//


    /**
     * 监控数据采集.
     * 1. 支持调用次数统计：count://host/interface?application=foo&method=foo&provider=10.20.153.11:20880&success=12&failure=2&elapsed=135423423
     * 1.1 host,application,interface,group,version,method 记录监控来源主机，应用，接口，方法信息。
     * 1.2 如果是消费者发送的数据，加上provider地址参数，反之，加上来源consumer地址参数。
     * 1.3 success,faulure,elapsed 记录距上次采集，调用的成功次数，失败次数，成功调用总耗时，平均时间将用总耗时除以成功次数。
     * <p>
     * [ INFO][20200722 17:10:37][main][ [DUBBO] Notify urls for subscribe url
     * consumer://192.168.43.245/com.walker.service.AreaService
     * ?application=service-provider
     * &category=providers,configurators,routers
     * &dubbo=2.5.3
     * &interface=com.walker.service.AreaService
     * &logger=log4j
     * &methods=findsRoot,saveAll,finds,get,deleteAll,count,delete
     * &owner=walker
     * &pid=15901&revision=1.0
     * &side=consumer&timeout=30000
     * &timestamp=1595409037320
     * &version=1.0
     * , urls: [
     * dubbo://192.168.43.245:8095/com.walker.service.AreaService
     * ?anyhost=true
     * &application=service-provider
     * &delay=-1&dubbo=2.5.3
     * &interface=com.walker.service.AreaService
     * &logger=log4j
     * &methods=findsRoot,saveAll,finds,get,deleteAll,count,delete
     * &owner=walker
     * &pid=15595
     * &revision=1.0
     * &side=provider
     * &timeout=30000
     * &timestamp=1595408938648
     * &version=1.0
     * , empty://192.168.43.245/com.walker.service.AreaService
     * ?application=service-provider
     * &category=configurators&dubbo=2.5.3
     * &interface=com.walker.service.AreaService
     * &logger=log4j
     * &methods=findsRoot,saveAll,finds,get,deleteAll,count,delete
     * &owner=walker&pid=15901
     * &revision=1.0&side=consumer
     * &timeout=30000
     * &timestamp=1595409037320
     * &version=1.0
     * , empty://192.168.43.245/com.walker.service.AreaService
     * ?application=service-provider
     * &category=routers&dubbo=2.5.3
     * &interface=com.walker.service.AreaService
     * &logger=log4j&methods=findsRoot,saveAll,finds,get,deleteAll,count,delete&owner=walker
     * &pid=15901&revision=1.0
     * &side=consumer&timeout=30000
     * &timestamp=1595409037320&version=1.0]
     * , dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]
     *
     * @param statistics
     */
    @Override
    public void collect(URL statistics) {
        queue.offer(statistics);
        if (log.isInfoEnabled()) {
            log.info("collect statistics: " + statistics);
        }

    }

    /**
     * 监控数据查询. 
     * 1. 支持按天查询：count://host/interface?application=foo&method=foo&side=provider&view=chart&date=2012-07-03
     * 1.1 host,application,interface,group,version,method 查询主机，应用，接口，方法的匹配条件，缺失的条件的表示全部，host用0.0.0.0表示全部。
     * 1.2 side=consumer,provider 查询由调用的哪一端采集的数据，缺省为都查询。
     * 1.3 缺省为view=summary，返回全天汇总信息，支持view=chart表示返回全天趋势图表图片的URL地址，可以进接嵌入其它系统的页面上展示。
     * 1.4 date=2012-07-03 指定查询数据的日期，缺省为当天。
     *
     * @param query
     * @return statistics
     */
    @Override
    public List<URL> lookup(URL query) {
        // TODO Auto-generated method stub
        return null;
    }
}
//dubbo初始化 提供者 服务日志
//[ INFO][20200723 09:53:21][main][ [DUBBO] The service ready on spring started. service: com.alibaba.dubbo.monitor.MonitorService, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.spring.ServiceBean.onApplicationEvent(ServiceBean.java:107)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Export dubbo service com.alibaba.dubbo.monitor.MonitorService to local registry, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ServiceConfig.exportLocal(ServiceConfig.java:510)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Export dubbo service com.alibaba.dubbo.monitor.MonitorService to url dubbo://192.168.43.245:8095/com.alibaba.dubbo.monitor.MonitorService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&logger=log4j&methods=lookup,collect&owner=walker&pid=19697&revision=2.5.3&side=provider&timeout=30000&timestamp=1595469201091&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ServiceConfig.doExportUrlsFor1Protocol(ServiceConfig.java:470)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Register dubbo service com.alibaba.dubbo.monitor.MonitorService url dubbo://192.168.43.245:8095/com.alibaba.dubbo.monitor.MonitorService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&logger=log4j&methods=lookup,collect&monitor=dubbo%3A%2F%2Flocalhost%3A8096%2Fcom.alibaba.dubbo.registry.RegistryService%3Fapplication%3Dservice-provider%26backup%3Dlocalhost%3A8888%26check%3Dfalse%26dubbo%3D2.5.3%26logger%3Dlog4j%26owner%3Dwalker%26pid%3D19697%26protocol%3Dregistry%26refer%3Ddubbo%253D2.5.3%2526interface%253Dcom.alibaba.dubbo.monitor.MonitorService%2526pid%253D19697%2526timestamp%253D1595469201109%26registry%3Dzookeeper%26timeout%3D30000%26timestamp%3D1595469201091&owner=walker&pid=19697&revision=2.5.3&side=provider&timeout=30000&timestamp=1595469201091&version=1.0 to registry registry://localhost:8096/com.alibaba.dubbo.registry.RegistryService?application=service-provider&backup=localhost:8888&check=false&dubbo=2.5.3&logger=log4j&owner=walker&pid=19697&registry=zookeeper&timeout=30000&timestamp=1595469201091, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ServiceConfig.doExportUrlsFor1Protocol(ServiceConfig.java:481)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Register: dubbo://192.168.43.245:8095/com.alibaba.dubbo.monitor.MonitorService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&logger=log4j&methods=lookup,collect&owner=walker&pid=19697&revision=2.5.3&side=provider&timeout=30000&timestamp=1595469201091&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.register(AbstractRegistry.java:302)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Subscribe: provider://192.168.43.245:8095/com.alibaba.dubbo.monitor.MonitorService?anyhost=true&application=service-provider&category=configurators&check=false&delay=-1&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&logger=log4j&methods=lookup,collect&owner=walker&pid=19697&revision=2.5.3&side=provider&timeout=30000&timestamp=1595469201091&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.subscribe(AbstractRegistry.java:325)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Notify urls for subscribe url provider://192.168.43.245:8095/com.alibaba.dubbo.monitor.MonitorService?anyhost=true&application=service-provider&category=configurators&check=false&delay=-1&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&logger=log4j&methods=lookup,collect&owner=walker&pid=19697&revision=2.5.3&side=provider&timeout=30000&timestamp=1595469201091&version=1.0, urls: [empty://192.168.43.245:8095/com.alibaba.dubbo.monitor.MonitorService?anyhost=true&application=service-provider&category=configurators&check=false&delay=-1&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&logger=log4j&methods=lookup,collect&owner=walker&pid=19697&revision=2.5.3&side=provider&timeout=30000&timestamp=1595469201091&version=1.0], dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]
//
//[ INFO][20200723 09:53:21][main][ [DUBBO] The service ready on spring started. service: com.walker.service.LogService, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.spring.ServiceBean.onApplicationEvent(ServiceBean.java:107)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Export dubbo service com.walker.service.LogService to local registry, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ServiceConfig.exportLocal(ServiceConfig.java:510)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Export dubbo service com.walker.service.LogService to url dubbo://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ServiceConfig.doExportUrlsFor1Protocol(ServiceConfig.java:470)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Register dubbo service com.walker.service.LogService url dubbo://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&monitor=dubbo%3A%2F%2Flocalhost%3A8096%2Fcom.alibaba.dubbo.registry.RegistryService%3Fapplication%3Dservice-provider%26backup%3Dlocalhost%3A8888%26check%3Dfalse%26dubbo%3D2.5.3%26logger%3Dlog4j%26owner%3Dwalker%26pid%3D19697%26protocol%3Dregistry%26refer%3Ddubbo%253D2.5.3%2526interface%253Dcom.alibaba.dubbo.monitor.MonitorService%2526pid%253D19697%2526timestamp%253D1595469201200%26registry%3Dzookeeper%26timeout%3D30000%26timestamp%3D1595469201188&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0 to registry registry://localhost:8096/com.alibaba.dubbo.registry.RegistryService?application=service-provider&backup=localhost:8888&check=false&dubbo=2.5.3&logger=log4j&owner=walker&pid=19697&registry=zookeeper&timeout=30000&timestamp=1595469201188, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ServiceConfig.doExportUrlsFor1Protocol(ServiceConfig.java:481)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Register: dubbo://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.register(AbstractRegistry.java:302)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Subscribe: provider://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&category=configurators&check=false&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.subscribe(AbstractRegistry.java:325)]
//[ INFO][20200723 09:53:21][main][ [DUBBO] Notify urls for subscribe url provider://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&category=configurators&check=false&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0, urls: [empty://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&category=configurators&check=false&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0], dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]


////消费者 服务加载日志
////先加载了monitor？
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][ [DUBBO] Register: consumer://192.168.43.245/com.alibaba.dubbo.monitor.MonitorService?category=consumers&check=false&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.register(AbstractRegistry.java:302)]
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][ [DUBBO] Subscribe: consumer://192.168.43.245/com.alibaba.dubbo.monitor.MonitorService?category=providers,configurators,routers&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.subscribe(AbstractRegistry.java:325)]
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][ [DUBBO] Notify urls for subscribe url consumer://192.168.43.245/com.alibaba.dubbo.monitor.MonitorService?category=providers,configurators,routers&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472, urls: [empty://192.168.43.245/com.alibaba.dubbo.monitor.MonitorService?category=providers&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472, empty://192.168.43.245/com.alibaba.dubbo.monitor.MonitorService?category=configurators&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472, empty://192.168.43.245/com.alibaba.dubbo.monitor.MonitorService?category=routers&dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472], dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]
////自定义dubbo单例 初始化 测试
//[ WARN][20200723 10:14:02][Cluster_Scheduler_Worker-1][dubbo singleton instance construct class com.walker.dubbo.DubboMgr$SingletonFactory path:dubbo-service-config.xml][][com.walker.dubbo.DubboMgr.start(DubboMgr.java:56)]
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][using logger: com.alibaba.dubbo.common.logger.log4j.Log4jLoggerAdapter][][]
//[ WARN][20200723 10:14:02][Cluster_Scheduler_Worker-1][com.walker.dubbo.DubboMgr begin][][com.walker.core.aop.ConnectorAdapter;.test(ConnectorAdapter;.java:25)]
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][dubbo is started ][][com.walker.dubbo.DubboMgr.doTest(DubboMgr.java:40)]
////测试用的服务echoService调用
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][ [DUBBO] Register: consumer://192.168.43.245/com.walker.service.EchoService?application=service-provider&category=consumers&check=false&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470442892&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.register(AbstractRegistry.java:302)]
//[ INFO][20200723 10:14:02][Cluster_Scheduler_Worker-1][ [DUBBO] Subscribe: consumer://192.168.43.245/com.walker.service.EchoService?application=service-provider&category=providers,configurators,routers&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470442892&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.subscribe(AbstractRegistry.java:325)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][ [DUBBO] Notify urls for subscribe url consumer://192.168.43.245/com.walker.service.EchoService?application=service-provider&category=providers,configurators,routers&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470442892&version=1.0, urls: [dubbo://192.168.43.245:8095/com.walker.service.EchoService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469200154&version=1.0, empty://192.168.43.245/com.walker.service.EchoService?application=service-provider&category=configurators&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470442892&version=1.0, empty://192.168.43.245/com.walker.service.EchoService?application=service-provider&category=routers&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470442892&version=1.0], dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][ [DUBBO] Refer dubbo service com.walker.service.EchoService from url zookeeper://localhost:8096/com.alibaba.dubbo.registry.RegistryService?anyhost=true&application=service-provider&check=false&delay=-1&dubbo=2.5.3&interface=com.walker.service.EchoService&logger=log4j&methods=echo&monitor=dubbo%3A%2F%2Flocalhost%3A8096%2Fcom.alibaba.dubbo.registry.RegistryService%3Fapplication%3Dservice-provider%26backup%3Dlocalhost%3A8888%26check%3Dfalse%26dubbo%3D2.5.3%26logger%3Dlog4j%26owner%3Dwalker%26pid%3D20661%26protocol%3Dregistry%26refer%3Ddubbo%253D2.5.3%2526interface%253Dcom.alibaba.dubbo.monitor.MonitorService%2526pid%253D20661%2526timestamp%253D1595470442902%26registry%3Dzookeeper%26timeout%3D30000%26timestamp%3D1595470442901&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470442892&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ReferenceConfig.createProxy(ReferenceConfig.java:423)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][serviceName:echoService cookie:null service:com.alibaba.dubbo.common.bytecode.proxy14@7906552e][][com.walker.dubbo.DubboMgr.getService(DubboMgr.java:81)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][echo:hello!][][com.walker.dubbo.DubboMgr.doTest(DubboMgr.java:42)]
////自测完毕
//[ WARN][20200723 10:14:03][Cluster_Scheduler_Worker-1][com.walker.dubbo.DubboMgr ok][][com.walker.core.aop.ConnectorAdapter;.test(ConnectorAdapter;.java:35)]
//[ WARN][20200723 10:14:03][Cluster_Scheduler_Worker-1][com.walker.dubbo.DubboMgr end][][com.walker.core.aop.ConnectorAdapter;.test(ConnectorAdapter;.java:39)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][ [DUBBO] Register: consumer://192.168.43.245/com.walker.service.LogService?application=service-provider&category=consumers&check=false&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470443060&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.register(AbstractRegistry.java:302)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][ [DUBBO] Subscribe: consumer://192.168.43.245/com.walker.service.LogService?application=service-provider&category=providers,configurators,routers&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470443060&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.subscribe(AbstractRegistry.java:325)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][ [DUBBO] Notify urls for subscribe url consumer://192.168.43.245/com.walker.service.LogService?application=service-provider&category=providers,configurators,routers&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470443060&version=1.0, urls: [dubbo://192.168.43.245:8095/com.walker.service.LogService?anyhost=true&application=service-provider&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=19697&revision=1.0&side=provider&timeout=30000&timestamp=1595469201189&version=1.0, empty://192.168.43.245/com.walker.service.LogService?application=service-provider&category=configurators&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470443060&version=1.0, empty://192.168.43.245/com.walker.service.LogService?application=service-provider&category=routers&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470443060&version=1.0], dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][ [DUBBO] Refer dubbo service com.walker.service.LogService from url zookeeper://localhost:8096/com.alibaba.dubbo.registry.RegistryService?anyhost=true&application=service-provider&check=false&delay=-1&dubbo=2.5.3&interface=com.walker.service.LogService&logger=log4j&methods=saveLogSocketModel,saveLogModel,saveStatis,saveLogModelNoTime&monitor=dubbo%3A%2F%2Flocalhost%3A8096%2Fcom.alibaba.dubbo.registry.RegistryService%3Fapplication%3Dservice-provider%26backup%3Dlocalhost%3A8888%26check%3Dfalse%26dubbo%3D2.5.3%26logger%3Dlog4j%26owner%3Dwalker%26pid%3D20661%26protocol%3Dregistry%26refer%3Ddubbo%253D2.5.3%2526interface%253Dcom.alibaba.dubbo.monitor.MonitorService%2526pid%253D20661%2526timestamp%253D1595470443061%26registry%3Dzookeeper%26timeout%3D30000%26timestamp%3D1595470443061&owner=walker&pid=20661&revision=1.0&side=consumer&timeout=30000&timestamp=1595470443060&version=1.0, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.config.ReferenceConfig.createProxy(ReferenceConfig.java:423)]
//[ INFO][20200723 10:14:03][Cluster_Scheduler_Worker-1][serviceName:logService cookie:null service:com.alibaba.dubbo.common.bytecode.proxy15@7d060763][][com.walker.dubbo.DubboMgr.getService(DubboMgr.java:81)]
////其他dubbo服务调用

//触发 monitorService  已重写实现 疑是消费者不能被调用内置monitor接口？白名单黑名单？
//[ INFO][20200723 10:20:02][DubboMonitorSendTimer-thread-2][ [DUBBO] Send statistics to monitor zookeeper://localhost:8096/com.alibaba.dubbo.monitor.MonitorService?dubbo=2.5.3&interface=com.alibaba.dubbo.monitor.MonitorService&pid=20661&timestamp=1595470424472, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.monitor.dubbo.DubboMonitor.send(DubboMonitor.java:80)]
//[ERROR][20200723 10:20:02][DubboMonitorSendTimer-thread-2][ [DUBBO] Unexpected error occur at send statistic, cause: Forbid consumer 192.168.43.245 access service com.alibaba.dubbo.monitor.MonitorService from registry localhost:8096 use dubbo version 2.5.3, Please check registry access list (whitelist/blacklist)., dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.monitor.dubbo.DubboMonitor$1.run(DubboMonitor.java:72)]
//        com.alibaba.dubbo.rpc.RpcException: Forbid consumer 192.168.43.245 access service com.alibaba.dubbo.monitor.MonitorService from registry localhost:8096 use dubbo version 2.5.3, Please check registry access list (whitelist/blacklist).
//        at
