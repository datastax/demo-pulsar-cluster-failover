package com.demo.adobe.astra.streaming.producer;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
public class StreamingProducer {

    private final Producer<byte[]> producer;

    @Autowired
    public StreamingProducer(Producer<byte[]> producer) {
        this.producer = producer;
    }

    public void produce() {
        IntStream.range(0, 100).forEach(i -> {
            String msg = Math.random() + " Message " + LocalDateTime.now();
            try {
                producer.newMessage().eventTime(System.currentTimeMillis()).value(msg.getBytes()).send();
                System.out.println("Message " + msg);
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        });
    }
}