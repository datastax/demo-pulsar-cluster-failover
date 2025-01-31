package com.demo.adobe.astra.streaming.controller;

import com.demo.adobe.astra.streaming.config.AppConfig;
import com.demo.adobe.astra.streaming.consumer.StreamingConsumer;
import com.demo.adobe.astra.streaming.producer.StreamingProducer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    private final PulsarClient client;
    private final AppConfig config;
    private final Producer<byte[]> producer;
    private long counter = 0;

    @Autowired
    public ApplicationController(AppConfig config, PulsarClient client, Producer<byte[]> producer, ApplicationContext applicationContext) {
        this.config = config;
        this.client = client;
        this.producer = producer;
    }

    @GetMapping("/produce")
    public String produce() throws PulsarClientException {
        StreamingProducer streamingProducer = new StreamingProducer(producer);
        streamingProducer.produce();
        return "Produce";
    }

    @GetMapping("/startConsumer")
    public String startConsumer() throws PulsarClientException {
        return "Started Consumer: " + new StreamingConsumer(config, client).startConsumer("Consumer" + (++counter));
    }

    @GetMapping("/failover")
    public String failover() {
        return "Successfully performed failover";
    }

    @GetMapping("/conn/close")
    public String close() throws PulsarClientException {
        client.close();
        return "Successfully closed connection";
    }

}
