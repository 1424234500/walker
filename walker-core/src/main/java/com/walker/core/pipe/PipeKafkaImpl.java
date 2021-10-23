package com.walker.core.pipe;

import com.walker.core.database.Kafka;
import com.walker.core.encode.MD5;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 使用kafka publish subscribe 传递
 *
 * 常用于多进程 多线程发布 多线程订阅 上下文隔离场景
 * 实现一对多 一人发布 多人复制处理
 *
 * 避免上下级影响
 * 只提供订阅后的 弱缓冲功能
 *
 *
 */
public class PipeKafkaImpl implements Pipe<String>{
	private static Logger log = LoggerFactory.getLogger(PipeKafkaImpl.class);

	/**
	 * 簇列
	 */
	private String key;

	/**
	 * 线程池消费 每个线程都去消费
	 */
	private ThreadPoolExecutor threadPool;

	@Override
	public void start(String key){
		this.key = key;

		try {
			Kafka.getInstance().topicCreate(key);
			log.info("Start res ok"  );
		} catch (ExecutionException e) {
			log.error("Start res exception " + e.getMessage(), e );
			throw new PipeException("start error " + e.getMessage());
		}
	}
	@Override
	public void stop(){
		log.info("stop");
	}

	@Override
	public boolean remove(String obj) {
		return false;
	}

	@Override
	public String get() {
		return null;
	}

	@Override
	public boolean put(Collection<String> objs) {
		for(String str : objs){
			put(str);
		}
		return true;
	}

	@Override
	public boolean put(String obj) {

		Kafka.doSendMessage(key, MD5.makeStr(obj), obj);

		return true;
	}

	@Override
	public boolean putHead(Collection<String> objs) {
		put(objs);
		return true;
	}

	@Override
	public boolean putHead(String obj) {
		put(obj);
		return true;
	}

	@Override
	public long size() {
		return -1;
	}

	/**
	 * 1线程 定时轮询  拿到资源 新建线程处理
	 * 多线程 各自轮询 消费 消费完后继续拿资源
	 */
	@Override
	public void startConsumer(int threadSize, final com.walker.core.aop.Fun<String> executer) {
		log.warn("StartConsumer");
		if(threadSize <= 0)return;
        threadSize += 1;
        threadPool = new ThreadPoolExecutor(threadSize, threadSize,0L, TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<Runnable>(PIPE_SIZE), new ThreadPoolExecutor.AbortPolicy());
		Consumer<String, String> consumer = Kafka.getInstance().getConsumer();
		consumer.subscribe(Collections.singletonList(key));
		threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while(!threadPool.isShutdown() ){
                    boolean ifWait = true;
                    if(threadPool.getQueue().size() < PIPE_SIZE){
                        try {
                            log.debug("kafka consumer:%s start poll! " + consumer.toString());
                            ConsumerRecords<String, String> records = consumer.poll(TIMEOUT_MS);
                            if (records != null && records.count() > 0) {
                                consumer.commitAsync();
                                for (ConsumerRecord<String, String> record : records) {
                                    log.debug("kafka consumer item topic:" + record.topic() + ", key:" + record.key() + ", value:" + record.value());
                                    threadPool.execute(new Runnable() {
                                        public void run() {
                                            executer.make(record.value());
                                        }
                                    });
                                }
                                ifWait = false;
                            }
                        } catch (Exception e) {
                            log.error("consumer error " + e.getMessage(), e);
                        }
                    }
                    if(ifWait){
                        try {
                            Thread.sleep(SLEEP_THREAD);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }

            }
        });

	}
	@Override
	public void stopConsumer() {
		if(threadPool != null && !threadPool.isShutdown()) {
			threadPool.shutdown();
		}
//        Consumer<String, String> consumer = Kafka.getInstance().getConsumer();
//        consumer.wakeup();

    }

	@Override
	public void await(long timeout, TimeUnit unit) {
		if(threadPool != null && !threadPool.isShutdown()) {
			try {
				threadPool.awaitTermination(timeout, unit);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

}
