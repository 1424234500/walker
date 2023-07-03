package com.walker.core.database;


import com.walker.core.aop.ConnectorAdapter;;
import com.walker.core.cache.Cache;
import com.walker.core.cache.ConfigMgr;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * kafka队列
 * 连接池管理
 * 提供连接 销毁连接
 * 单例
 *
 * @author walker
 */
public class Kafka extends ConnectorAdapter; {
	private static final Logger log = LoggerFactory.getLogger(Kafka.class);

	private final Properties properties = new Properties();
	private final AtomicInteger sendCount = new AtomicInteger(0);
	private final int BATCH_SIZE = 2000;
	private final int BATCH_TIME = 5000;
	/**
	 * 连接数计数器
	 */
	int cc = 0;
	/**
	 * 查询次数计数器
	 */
	int get = 0;
	private Producer<String, String> producer;
	private Consumer<String, String> consumer;
	private AdminClient adminClient;
	private long lastCommitTime = 0L;

	private Kafka() {
	}

	/**
	 * 回调环绕执行 kafka 操作
	 */
	public static Future<RecordMetadata> doProducer(Fun fun) {
		Future<RecordMetadata> res = null;
		if (fun != null) {
			Producer<String, String> producer = Kafka.getInstance().getProducer();
			try {
				res = fun.make(producer);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				throw e;
			} finally {
				Kafka.getInstance().close(producer);
			}
		} else {
			log.error("fun is null");
		}
		return res;
	}

	/**
	 * 回调环绕执行 kafka 操作
	 */
	public static Future<RecordMetadata> doSendMessage(String topic, String key, String value) {
		return doProducer(new Fun() {
			@Override
			public Future<RecordMetadata> make(Producer<String, String> producer) {
				Future<RecordMetadata> res = producer.send(new ProducerRecord<String, String>(topic, key, value));

//				是否手动控制(还需要线程轮询状态超时) 批量次数 批量时间 kafka自己配置
//				producerCommit(producer, 1);

				return res;
			}
		});
	}

	public static Kafka getInstance() {
		return SingletonFactory.instance;
	}

	public Kafka setProperties(String key, String value) {
		this.properties.setProperty(key, value);
		return this;
	}

	public Properties getProperties() {
		return (Properties) this.properties.clone();
	}

	public AdminClient getAdminClient() {
		if (this.adminClient == null) {
			synchronized (this) {
				if (this.adminClient == null) {
					this.adminClient = KafkaAdminClient.create(properties);
				}
			}
		}
		return this.adminClient;
	}

	public Producer<String, String> getProducer() {
		if (this.producer == null) {
			synchronized (this) {
				if (this.producer == null) {
					this.producer = new KafkaProducer<String, String>(properties);
				}
			}
		}
		return this.producer;
	}

	public Consumer getConsumer() {
		if (this.consumer == null) {
			synchronized (this) {
				if (this.consumer == null) {
					this.consumer = new KafkaConsumer<String, String>(properties);
				}
			}
		}
		return this.consumer;
	}

	private void producerCommit(Producer<String, String> producer, int c) {
		sendCount.addAndGet(c);
		int cacheSize = sendCount.get();
		if (cacheSize > BATCH_SIZE || System.currentTimeMillis() - this.lastCommitTime > BATCH_TIME) {
			synchronized (producer) {
				log.debug("flush producer:" + cacheSize + ", lastTime:" + this.lastCommitTime);
				producer.flush();
				this.lastCommitTime = System.currentTimeMillis();
				this.sendCount.set(0);
			}
		}
	}

	public void close(Producer<String, String> kafka) {
		if (kafka != null) {
//			kafka.close();
			get--;
		}
	}

	/**
	 * 从缓存获取并初始化
	 */
	public boolean doInit() {
		Cache<String> cache = ConfigMgr.getInstance();
//		ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG	//静态配置常量key
//		公用
		properties.put("bootstrap.servers", cache.get("kafka_bootstrap.servers", "127.0.0.1:8097"));//xxx服务器ip ,1xxx:9092,xxx:9092
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

//		consumer
		properties.put("auto.offset.reset", "latest");
		properties.put("enable.auto.commit", "true");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("group.id", "test");    //消费分组id

//        properties.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RoundRobinAssignor");

//		producer
		properties.put("acks", "all");//所有follower都响应了才认为消息提交成功，即"committed"
		properties.put("retries", 0);//retries = MAX 无限重试，直到你意识到出现了问题:)
		properties.put("batch.size", 16384);//producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数 batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
		properties.put("linger.ms", 1);//延迟1ms发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
		properties.put("buffer.memory", 33554432);//producer可以用来缓存数据的内存大小。


		log.info("kafka init ----------------------- " + cc++);
		check();
		log.info(this.toString());
		if (cc > 1) {
			log.error("----------------------------");
			log.error("---------单例失败？？？？？------------");
			log.error("----------------------------");
		}
		return true;
	}

	@Override
	public boolean doUninit() {
		log.warn("uninit");
		return super.doUninit();
	}

	@Override
	public boolean doTest() {
		return getProducer() != null;
	}

	public String toString() {
		return "kafka " + properties.toString();
	}

	/**
	 * 自动提交offset
	 */
	public void commitAuto(List<String> topics) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records)
				System.err.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		}
	}

	/**
	 * 手动提交offset
	 */
	public void commitControl(List<String> topics) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		final int minBatchSize = 2;
		List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(1000);
			for (ConsumerRecord<String, String> record : records) {
				buffer.add(record);
			}
			if (buffer.size() >= minBatchSize) {
				/////////////////////////////////////////do yourself
				// 阻塞同步提交
				consumer.commitSync();
				buffer.clear();
			}
		}
	}

	/**
	 * 手动设置分区
	 */
	public void setOffSet(List<String> topics) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
			// 处理每个分区消息后, 提交偏移量
			for (TopicPartition partition : records.partitions()) {
				List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);

				for (ConsumerRecord<String, String> record : partitionRecords) {
					System.out.println(record.offset() + ": " + record.value());
				}
				long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
				consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
			}
		}
	}

	/**
	 * 手动设置消息offset
	 */
	public void setSeek(List<String> topics) {
		Properties props = getProperties();
		props.put("enable.auto.commit", "false");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(topics);
		consumer.seek(new TopicPartition("http_zhixin", 0), 797670770);
		ConsumerRecords<String, String> records = consumer.poll(100);

		for (ConsumerRecord<String, String> record : records) {
			System.err.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
			consumer.commitSync();
		}

	}

	//	创建adminClient
//	创建topic
//	注意kafka topic的名字不可以包含中文字符，不然是无法创建的。分区数，通常建议为12，其初期最好评估好消费节点数和能力来设置此值；副本数，建议值是2-3，可根据业务需求和发送延迟需要进行设置，影响数据可靠性。
//	重复创建已有的topic会抛出异常。
	public void topicCreate(String topicName) throws ExecutionException {
		try {
//			String name, int numPartitions, short replicationFactor 单节点只能单备份
			NewTopic newTopic = new NewTopic(topicName, 12, (short) 1);
			getAdminClient().createTopics(Collections.singleton(newTopic), new CreateTopicsOptions().timeoutMs(10000))
					.all()
					.get();
		} catch (ExecutionException e) {
			if (e.getMessage().startsWith("org.apache.kafka.common.errors.TopicExistsException")) {
				log.warn(topicName + " topic is exist !! " + e.getMessage());
			} else {
				throw e;
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	//	查询topic列表
	public List<String> topicGet(int secondsTimeout) throws InterruptedException, ExecutionException, TimeoutException {
		List<String> list = new ArrayList<>();
		getAdminClient().listTopics()
				.listings()
				.get(secondsTimeout, TimeUnit.SECONDS)
				.forEach(topicListing -> {
					log.warn("currentTopic is: " + topicListing.name());
					list.add(topicListing.name());
				});
		return list;
	}

	//	删除topic
	public void topicDelete(String... topicName) throws ExecutionException, InterruptedException {
		getAdminClient().deleteTopics(Arrays.asList(topicName))
				.all()
				.get();
	}

	/**
	 * jedis获取 执行后 自动关闭
	 */
	public interface Fun {
		Future<RecordMetadata> make(Producer<String, String> producer);
	}

	//单例
	private static class SingletonFactory {
		static Kafka instance;

		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			instance = new Kafka();
			instance.init();
		}
	}


}	
