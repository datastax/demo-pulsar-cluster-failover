package com.demo.adobe.astra.streaming.config;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.ControlledClusterFailover;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:pulsar-config.properties")
public class AppConfig {

    @Value("${PRIMARY_CLUSTER_ID}")
    public String PRIMARY_CLUSTER_ID;
    @Value("${SECONDARY_CLUSTER_ID}")
    public String SECONDARY_CLUSTER_ID;
    @Value("${WEB_SERVICE_URL}")
    public String WEB_SERVICE_URL;
    @Value("${BROKER_SERVICE_URL}")
    public String BROKER_SERVICE_URL;
    @Value("${NAMESPACE}")
    public String NAMESPACE;
    @Value("${TENANT}")
    public String TENANT;
    @Value("${TOPIC}")
    public String TOPIC;
    @Value("${SUBSCRIPTION_NAME}")
    public String SUBSCRIPTION_NAME;
    @Value("${URL_PROVIDER}")
    public String URL_PROVIDER;
    @Value("${DEFAULT_SERVICE_URL}")
    public String DEFAULT_SERVICE_URL;
    @Value("${DEFAULT_AUTH_PARAM")
    public String DEFAULT_AUTH_PARAM;

    @Bean(name = "pulsarClient")
    public PulsarClient client() throws Exception {
        System.out.println("Creating Pulsar client");

        //Cluster level Failover logic
        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/json");
//        header.put("Authorization", DEFAULT_AUTH_PARAM);

        ServiceUrlProvider provider =
                ControlledClusterFailover.builder()
                        .defaultServiceUrl(DEFAULT_SERVICE_URL)
                        .checkInterval(1, TimeUnit.MINUTES)
                        .urlProvider(URL_PROVIDER)
//                        .urlProviderHeader(header)
                        .build();

        System.out.println("Provider details: " + provider);
        PulsarClient client = PulsarClient.builder()
                .serviceUrlProvider(provider)
                .connectionTimeout(5, TimeUnit.MINUTES)
                .operationTimeout(5, TimeUnit.MINUTES)
                .build();
        provider.initialize(client);
        System.out.println("Pulsar client initialized: "+client);
        return client;

        /*return PulsarClient.builder()
                .serviceUrl(PRIMARY_BROKER_SERVICE_URL)
                .authentication(AuthenticationFactory.token(PRIMARY_PULSAR_TOKEN))
                .build();*/
    }

    /*@Bean
    public PulsarAdmin adminClient() throws Exception{
        System.out.println("Creating admin client");
        return PulsarAdmin.builder().serviceHttpUrl(PRIMARY_WEB_SERVICE_URL).authentication(AuthenticationFactory.token(PRIMARY_PULSAR_TOKEN)).build();
    }*/

    @Bean
    public Producer<byte[]> startProducer() throws Exception {
        System.out.println("Starting producer...");
        return client().newProducer()
                .topic("persistent://" + TENANT + "/" + NAMESPACE + "/" + TOPIC)
                .create();
    }

    @Bean
    public String consumerName() {
        return "";
    }

}
