package com.walker.core.database;

import com.walker.util.Tools;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class KafkaTest {

    @Test
    public void doKafka() throws ExecutionException, InterruptedException {
        for(String topicName : "hello,hello2,hello3".split(",")) {
            RecordMetadata a = Kafka.doProducer(new Kafka.Fun () {
                @Override
                public Future<RecordMetadata> make(Producer<String, String> producer) {
                    return producer.send(new ProducerRecord<String, String>(topicName, topicName));
                }
            }).get();
            Tools.out(topicName, topicName, a.partition(), a.offset(), a.serializedKeySize(), a.serializedValueSize(), a.timestamp(), a.topic(), a.toString());
        }



    }

    @Test
    public void topicTest() throws ExecutionException, TimeoutException, InterruptedException {
        Kafka.getInstance().topicCreate("h1");
        Kafka.getInstance().topicCreate("h2");
        Tools.out(Kafka.getInstance().topicGet(10));
        Kafka.getInstance().topicDelete("h1", "h2");



    }



}