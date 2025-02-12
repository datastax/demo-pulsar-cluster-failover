package com.demo.adobe.astra.streaming.producer;

import com.demo.adobe.astra.streaming.config.AppConfig;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
public class StreamingProducer {

    private final AppConfig config;
    private final Producer<byte[]> producer;

    @Autowired
    public StreamingProducer(AppConfig config, Producer<byte[]> producer) {
        this.config = config;
        this.producer = producer;
    }

    public void produce() {
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
}