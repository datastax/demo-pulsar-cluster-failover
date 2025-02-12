package com.demo.adobe.astra.streaming.consumer;

import com.demo.adobe.astra.streaming.config.AppConfig;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StreamingConsumer {

    private final AppConfig config;
    private final PulsarClient client;

    @Autowired
    public StreamingConsumer(AppConfig config, PulsarClient client) {
        this.config = config;
        this.client = client;
    }

    public Consumer<byte[]> consumer(String name) throws PulsarClientException {
        if (name == null || name.isEmpty()) {
            name = "Consumer1";
        }
        System.out.println("Starting consumer..." + name);
        return client.newConsumer()
                .consumerName(name)
                .topic("persistent://" + config.TENANT + "/" + config.NAMESPACE + "/" + config.TOPIC)
                .subscriptionName(config.SUBSCRIPTION_NAME)
                .subscriptionType(SubscriptionType.Exclusive)
                .replicateSubscriptionState(true)
                .messageListener((consumer, message) -> {
                    try {
                        System.out.println("Consumer:" + consumer.getConsumerName() + ", Read: " + new String(message.getData()));
                        consumer.acknowledge(message);
                    } catch (PulsarClientException e) {
                        throw new RuntimeException(e);
                    }})
                .subscribe();
    }

    @Bean
    public String startConsumer(String name) throws PulsarClientException {
        return consumer(name).getConsumerName();
    }

}
