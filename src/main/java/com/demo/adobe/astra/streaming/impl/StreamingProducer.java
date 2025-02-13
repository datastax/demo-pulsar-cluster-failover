package com.demo.adobe.astra.streaming.impl;

import com.demo.adobe.astra.streaming.config.AppConfig;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
public class StreamingProducer {

    private final AppConfig config;
    private final PulsarClient client;

    @Autowired
    public StreamingProducer(AppConfig config, PulsarClient client) {
        this.config = config;
        this.client = client;
    }

    @Bean
    public Producer<byte[]> producer (String producerName) throws Exception {
        if (producerName == null || producerName.isEmpty()) {
            producerName = "producerName1";
        }
        System.out.println("Starting producer..."+producerName);
        return client.newProducer()
                .producerName(producerName)
                .topic("persistent://" + config.TENANT + "/" + config.NAMESPACE + "/" + config.TOPIC)
                .create();
    }

    public void produce(Producer<byte[]> producer) {
        IntStream.range(0, 100).forEach(i -> {
            String msg = Math.random() + " Message " + LocalDateTime.now();
            try {
                producer.newMessage()
                        .eventTime(System.currentTimeMillis())
                        .value(msg.getBytes())
                        //.replicationClusters(Arrays.asList(config.PRIMARY_CLUSTER_ID,config.SECONDARY_CLUSTER_ID))
                        .send();
                System.out.println("Producer:"+producer.getProducerName()+", Message " + msg);
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /*@Bean
    public String startProducer(String producerName) throws Exception {
        return producer(producerName).getProducerName();
    }*/
}