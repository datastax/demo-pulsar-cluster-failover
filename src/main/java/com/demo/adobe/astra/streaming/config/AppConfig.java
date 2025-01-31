package com.demo.adobe.astra.streaming.config;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.ServiceUrlProvider;
import org.apache.pulsar.client.impl.ControlledClusterFailover;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:config/pulsar-config.properties")
public class AppConfig {

    @Value("${PRIMARY_PULSAR_TOKEN}")
    public String PRIMARY_PULSAR_TOKEN;
    @Value("${SECONDARY_PULSAR_TOKEN}")
    public String SECONDARY_PULSAR_TOKEN;

    @Value("${WEB_SERVICE_URL}")
    public String WEB_SERVICE_URL;
    @Value("${NAMESPACE}")
    public String NAMESPACE;
    @Value("${TENANT}")
    public String TENANT;
    @Value("${TOPIC}")
    public String TOPIC;
    @Value("${SUBSCRIPTION_NAME}")
    public String SUBSCRIPTION_NAME;
    @Value("${FAILOVER_URL}")
    public String FAILOVER_URL;

    @Bean
    public PulsarClient client() throws Exception {

        Map<String, String> header = new HashMap<>();
        header.put("service_user_id", "abhishek.goswami@datastax.com");
        header.put("service_password", "j,-y5Yg4B+#8+*jq");
        header.put("primary", PRIMARY_PULSAR_TOKEN);
        header.put("secondary", SECONDARY_PULSAR_TOKEN);

        ServiceUrlProvider provider =
                ControlledClusterFailover.builder()
                        .defaultServiceUrl(WEB_SERVICE_URL)
                        .checkInterval(1, TimeUnit.MINUTES)
                        .urlProvider(FAILOVER_URL)
                        .urlProviderHeader(header)
                        .build();

        PulsarClient client = PulsarClient.builder().serviceUrlProvider(provider).build();
        provider.initialize(client);

        return client;
    }

    @Bean
    public Producer<byte[]> producer(PulsarClient client) throws PulsarClientException {
        System.out.println("Starting producer...");
        return client.newProducer()
                .topic("persistent://" + TENANT + "/" + NAMESPACE + "/" + TOPIC)
                .create();
    }

    @Bean
    public String consumerName() {
        return "";
    }

}
