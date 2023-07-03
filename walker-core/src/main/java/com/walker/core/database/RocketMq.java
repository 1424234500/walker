package com.walker.core.database;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.walker.core.aop.ConnectorAdapter;
import com.walker.core.cache.ConfigMgr;
import com.walker.core.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * rocketMq 接入工具模型
 * <p>
 * eg: *test
 * <dependency>
 * <groupId>com.alibaba.rocketmq</groupId>
 * <artifactId>rocketmq-client</artifactId>
 * <version>3.5.8</version>
 * </dependency>
 * <dependency>
 * <groupId>com.alibaba.rocketmq</groupId>
 * <artifactId>rocketmq-all</artifactId>
 * <version>3.5.8</version>
 * </dependency>
 */
public class RocketMq extends ConnectorAdapter {
    private final static Logger log = LoggerFactory.getLogger(RocketMq.class);

    private DefaultMQProducer producer;
    private DefaultMQPushConsumer consumer;
    private String group = "group";
    private String instanceName = "instanceName";
    private String namesrvAddr = "127.0.0.1:9876";

    /**
     * 单例模式
     */
    public static RocketMq getInstance() {
        return SingletonFactory.instance;
    }

    public static void main(String[] argv) throws Exception {
//        模拟测试2个应用 2个topic
//
//
        int consumerGroupSize = 3; //3个group

//     生产 topic message
        ThreadUtil.scheduleWithFixedDelay(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                String res = null;
                try {
                    res = RocketMq.getInstance().doProducer(producer -> {
                        String str = (" hello rocket " + count++);
                        producer.send(new Message("hello", str.getBytes()));
                        System.out.println(Thread.currentThread().getName() + " product " + str);

                        return "";
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3, 1, TimeUnit.SECONDS);


// 消费 topic message
        new RocketMq().doConsumer(consumer -> {
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    for (MessageExt me : list) {
                        System.out.println(Thread.currentThread().getName() + " consumer " + new String(me.getBody()));
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.subscribe("hello", "*");
            consumer.start();

            return null;
        });


    }

    @Override
    public String info() {
        return this.toString();
    }

    @Override
    public Boolean check() throws Exception {
        String res = this.doProducer(DefaultMQProducer::toString);
        log.info("test res " + res);
        return super.check();
    }

    @Override
    public Boolean init() throws Exception {
        return super.init();
    }

    @Override
    public Boolean uninit() throws Exception {
        if (this.producer != null) {
            this.producer.shutdown();
        }
        return super.uninit();
    }

    /**
     * 回调环绕执行 操作
     */
    public <T> T doProducer(Fun<T, DefaultMQProducer> fun) throws Exception {
        if (fun != null) {
            DefaultMQProducer producer = this.getProducer();
            try {
                return fun.make(producer);
            } finally {
//                jedis.close();
            }
        }
        return null;
    }

    /**
     * 回调环绕执行 操作
     */
    public <T> T doConsumer(Fun<T, DefaultMQPushConsumer> fun) throws Exception {
        if (fun != null) {
            DefaultMQPushConsumer consumer = this.getConsumer();
            try {
                return fun.make(consumer);
            } finally {
//                jedis.close();
            }
        }
        return null;
    }

    /**
     * 对象内单例 迟加载 双重锁
     */
    private DefaultMQProducer getProducer() {
        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    log.info("init producer begin " + this);
                    this.producer = new DefaultMQProducer(this.group);
                    this.producer.setNamesrvAddr(this.namesrvAddr);
                    this.producer.setInstanceName(this.instanceName);
                    try {
                        this.producer.start();
                    } catch (MQClientException e) {
                        throw new RuntimeException(this + " " + e.getMessage(), e);
                    }
//                    producer.send(new Message("qch_20170706","Hello".getBytes()));
//                    producer.shutdown();
                    log.info("init producer ok " + this);
                }
            }
        }
        return this.producer;
    }

    /**
     * 对象内单例 迟加载 双重锁
     */
    private DefaultMQPushConsumer getConsumer() {
        if (this.consumer == null) {
            synchronized (this) {
                if (this.consumer == null) {
                    log.info("init consumer begin " + this);
                    this.consumer = new DefaultMQPushConsumer(this.group);
                    this.consumer.setNamesrvAddr(this.namesrvAddr);
                    this.consumer.setInstanceName(this.instanceName);
//                    try{
//                        this.consumer.start(); //需要先启动注册
//                    }catch(MQClientException e) {
//                        throw new RuntimeException(this.toString() + " " + e.getMessage(), e);
//                    }
//                    producer.send(new Message("qch_20170706","Hello".getBytes()));
//                    producer.shutdown();
                    log.info("init consumer ok " + this);
                }
            }
        }
        return this.consumer;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public RocketMq setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public RocketMq setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
        return this;
    }

    public String getProducerProducerGroup() {
        return group;
    }

    public RocketMq setDefaultMQProducerProducerGroup(String producerGroup) {
        this.group = producerGroup;
        return this;
    }

    @Override
    public String toString() {
        return "RocketMq{" +
                "instanceName='" + instanceName + '\'' +
                ", namesrvAddr='" + namesrvAddr + '\'' +
                ", group='" + group + '\'' +
                '}';
    }

    public interface Fun<RES, ARGS> {
        RES make(ARGS args) throws Exception;
    }

    private static class SingletonFactory {
        static RocketMq instance;

        static {
            ConfigMgr cache = ConfigMgr.getInstance();

            log.warn("singleton instance construct " + SingletonFactory.class);

            instance = new RocketMq();
            instance.setNamesrvAddr(cache.get("RocketMq.namesrvAddr", instance.namesrvAddr));
            instance.setInstanceName(cache.get("RocketMq.instanceName", instance.instanceName));
        }
    }


}
