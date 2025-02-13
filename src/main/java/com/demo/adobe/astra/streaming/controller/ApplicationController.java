package com.demo.adobe.astra.streaming.controller;

import com.demo.adobe.astra.streaming.config.AppConfig;
import com.demo.adobe.astra.streaming.impl.StreamingConsumer;
import com.demo.adobe.astra.streaming.impl.StreamingProducer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    private final AppConfig config;
    private final PulsarClient client;
    private final Producer<byte[]> producer;
    private long producerNo = 1;
    private long consumerNo = 1;

    @Autowired
    public ApplicationController(AppConfig config, PulsarClient client, Producer<byte[]> producer) {
        this.config = config;
        this.client = client;
        this.producer = producer;
    }

    @GetMapping("/produce")
    public String produce() {
        StreamingProducer streamingProducer = new StreamingProducer(config,client);
        streamingProducer.produce(producer);
        return "Produce";
    }

    @GetMapping("/startProducer")
    public String startProducer() throws Exception {
        return "Started Consumer: " + new StreamingProducer(config,client).producer("Consumer" + (++producerNo));
    }

    @GetMapping("/startConsumer")
    public String startConsumer() throws Exception {
        return "Started Consumer: " + new StreamingConsumer(config,client).startConsumer("Consumer" + (++consumerNo));
    }

    @GetMapping("/conn/close")
    public String close() throws Exception {
        client.close();
        return "Successfully closed connection";
    }

}
