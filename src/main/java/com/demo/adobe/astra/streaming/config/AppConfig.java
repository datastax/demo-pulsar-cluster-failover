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

@Configuration
@PropertySource("classpath:pulsar-config.properties")
public class AppConfig {

    @Value("${PRIMARY_PULSAR_TOKEN}")
    public String PRIMARY_PULSAR_TOKEN;
    @Value("${PRIMARY_CLUSTER_ID}")
    public String PRIMARY_CLUSTER_ID;
    @Value("${PRIMARY_WEB_SERVICE_URL}")
    public String PRIMARY_WEB_SERVICE_URL;
    @Value("${PRIMARY_BROKER_SERVICE_URL}")
    public String PRIMARY_BROKER_SERVICE_URL;
    @Value("${SECONDARY_PULSAR_TOKEN}")
    public String SECONDARY_PULSAR_TOKEN;
    @Value("${SECONDARY_CLUSTER_ID}")
    public String SECONDARY_CLUSTER_ID;
    @Value("${SECONDARY_BROKER_SERVICE_URL}")
    public String SECONDARY_BROKER_SERVICE_URL;

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
    @Value("${AUTH_PLUGIN_CLASS}")
    public String AUTH_PLUGIN_CLASS;

    @Bean
    public PulsarClient client() throws Exception {
        System.out.println("Creating Pulsar client");

        //Cluster level Failover logic
        Map<String, String> header = new HashMap<>();
        header.put(PRIMARY_CLUSTER_ID, PRIMARY_PULSAR_TOKEN);
        header.put(SECONDARY_CLUSTER_ID, SECONDARY_PULSAR_TOKEN);

        ServiceUrlProvider provider =
                ControlledClusterFailover.builder()
                        .defaultServiceUrl(PRIMARY_BROKER_SERVICE_URL)
                        //.checkInterval(30, TimeUnit.MINUTES)
                        .urlProvider(URL_PROVIDER)
                        .urlProviderHeader(header)
                        .build();

        System.out.println("Provider details: " + provider);
        PulsarClient client = PulsarClient.builder().serviceUrlProvider(provider).build();
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
