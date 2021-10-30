package com.walker.dao;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SentinelLimitDao implements Limiter {
    private static Logger log = LoggerFactory.getLogger(SentinelLimitDao.class);

    @Autowired
    RedisDao redisDao;
    @Autowired
    JdbcDao jdbcDao;
    @Autowired
    ConfigDao configDao;

    /**
     *
     * 穿透？  等待锁超时怎么办
     *
     * 并发访问 等待获取数据库 加锁 等待多一些时间
     *
     * 配置缓存10小时 或者永不过期？ 等待变化时通知清理缓存
     * 并发访问配置时 多等待充足时间 等待其他进程线程查询 10s
     *
     */
    @Override
    public String tryAcquire(String url, String userId) {
        String res = "";
        // 1.5.0 版本开始可以直接利用 try-with-resources 特性
        //@SentinelResource("HelloWorld")
        try (Entry entry = SphU.entry(url)) {
            // 被保护的逻辑
            log.debug("SentinelLimitDao ok " + url + " " + userId);
        } catch (BlockException ex) {
            // 处理被流控的逻辑
            res = "SentinelLimitDao out of " + url + " " + userId + " " + ex.getRuleLimitApp() + " " + ex.getRule() + " " + ex.toString();
            log.warn(res);
        }
        return res;
    }


    /**
     * sentinel 限流熔断框架初始话
     * 配置见dml.sql
     */
    public int reload(){
        int res = -1;
        log.info(Config.getPre() + "initSentinel!!! ");
//        FlowRuleManager.loadRules(List<FlowRule> rules); // 修改流控规则
//        DegradeRuleManager.loadRules(List<DegradeRule> rules); // 修改降级规则
//        SystemRuleManager.loadRules(List<SystemRule> rules); // 修改系统规则
//        AuthorityRuleManager.loadRules(List<AuthorityRule> rules); // 修改授权规则
        List<FlowRule> flowRules = new ArrayList<>();
        List<DegradeRule> degradeRules = new ArrayList<>();
        List<SystemRule> systemRules = new ArrayList<>();

//        系统配置
//        系统保护规则是从应用级别的入口流量进行控制，从单台机器的总体 Load、RT、入口 QPS 和线程数四个维度监控应用数据，让系统尽可能跑在最大吞吐量的同时保证系统整体的稳定性。
//        系统保护规则是应用整体维度的，而不是资源维度的，并且仅对入口流量生效。入口流量指的是进入应用的流量（EntryType.IN），比如 Web 服务或 Dubbo 服务端接收的请求，都属于入口流量。
//        系统规则支持四种阈值类型：
//        Load（仅对 Linux/Unix-like 机器生效）：当系统 load1 超过阈值，且系统当前的并发线程数超过系统容量时才会触发系统保护。系统容量由系统的 maxQps * minRt 计算得出。设定参考值一般是 CPU cores * 2.5。
//        RT：当单台机器上所有入口流量的平均 RT 达到阈值即触发系统保护，单位是毫秒。
//        线程数：当单台机器上所有入口流量的并发线程数达到阈值即触发系统保护。
//        入口 QPS：当单台机器上所有入口流量的 QPS 达到阈值即触发系统保护。
//        参数说明:
//                highestSystemLoad	最大的 load1，参考值	-1 (不生效)
//                avgRt	所有入口流量的平均响应时间	-1 (不生效)
//                maxThread	入口流量的最大并发数	-1 (不生效)
//                qps	所有入口资源的 QPS	-1 (不生效)
        SystemRule systemRule = new SystemRule();
        systemRule.setHighestSystemLoad(configDao.get("com.walker.dao.SentinelLimitDao.reload.HighestSystemLoad", Runtime.getRuntime().availableProcessors() * 2.5));
        systemRule.setHighestCpuUsage(configDao.get("com.walker.dao.SentinelLimitDao.reload.HighestCpuUsage", 80));
        systemRule.setAvgRt(configDao.get("com.walker.dao.SentinelLimitDao.reload.AvgRt", 30 * 1000L));
        systemRule.setMaxThread(configDao.get("com.walker.dao.SentinelLimitDao.reload.MaxThread", 998L));
        systemRules.add(systemRule);

//      查表初始化
        String url = "/comm/getColsMap.do";

//        熔断配置
//        我们通常用以下几种方式来衡量资源是否处于稳定的状态：
//        平均响应时间 (DEGRADE_GRADE_RT)：当资源的平均响应时间超过阈值（DegradeRule 中的 count，以 ms 为单位）之后，资源进入准降级状态。如果接下来 1s 内持续进入 5 个请求（即 QPS >= 5），它们的 RT 都持续超过这个阈值，那么在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地熔断（抛出 DegradeException）。注意 Sentinel 默认统计的 RT 上限是 4900 ms，超出此阈值的都会算作 4900 ms，若需要变更此上限可以通过启动配置项 -Dcsp.sentinel.statistic.max.rt=xxx 来配置。
//        异常比例 (DEGRADE_GRADE_EXCEPTION_RATIO)：当资源的每秒异常总数占通过量的比值超过阈值（DegradeRule 中的 count）之后，资源进入降级状态，即在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，对这个方法的调用都会自动地返回。异常比率的阈值范围是 [0.0, 1.0]，代表 0% - 100%。
//        异常数 (DEGRADE_GRADE_EXCEPTION_COUNT)：当资源近 1 分钟的异常数目超过阈值之后会进行熔断。注意由于统计时间窗口是分钟级别的，若 timeWindow 小于 60s，则结束熔断状态后仍可能再进入熔断状态。 详情参考：熔断降级 2
        DegradeRule degradeRule = new DegradeRule();
        degradeRule.setResource(url);
        // set threshold RT, 10 ms
        degradeRule.setCount(configDao.get("com.walker.dao.SentinelLimitDao.reload.DegradeRule.Count", 0.80D));
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        degradeRule.setTimeWindow(configDao.get("com.walker.dao.SentinelLimitDao.reload.DegradeRule.TimeWindow", 5));
        degradeRules.add(degradeRule);
//        sentinel限流配置
//        double qps = configDao.get(url, 1D);
//        if(qps >= 0) {
//            FlowRule rule = new FlowRule();
//            rule.setResource(url);
//            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
//            rule.setCount(1);// Set limit QPS to 20.
//            flowRules.add(rule);
//        }


        FlowRuleManager.loadRules(flowRules);
        DegradeRuleManager.loadRules(degradeRules);
        SystemRuleManager.loadRules(systemRules);


        return res;
    }
}
