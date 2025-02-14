package com.demo.adobe.astra.streaming.config;

import org.apache.pulsar.client.api.AuthenticationFactory;
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
@PropertySource("classpath:pulsar-config.properties")
public class AppConfig {

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
    @Value("${DEFAULT_AUTH_PARAM}")
    public String DEFAULT_AUTH_PARAM;

    @Bean
    public PulsarClient client() {
        System.out.println("Creating Pulsar client");

        //Cluster level Failover logic
        Map<String, String> headers = new HashMap<>();
        headers.put("clusterA", "");
        headers.put("clusterB", "");

        PulsarClient client = null;
        try {
            ServiceUrlProvider provider =
                    ControlledClusterFailover.builder()
                            .defaultServiceUrl(DEFAULT_SERVICE_URL)
                            .checkInterval(5, TimeUnit.SECONDS)
                            .urlProvider(URL_PROVIDER)
                            .urlProviderHeader(headers)
                            .build();

            System.out.println("Provider details: " + provider);
            client = PulsarClient.builder()
                    .serviceUrlProvider(provider)
                    .build();
            TimeUnit.SECONDS.sleep(6);
            provider.initialize(client);
            System.out.println("Pulsar client initialized: " + client);
            return client;
        } catch (Exception e) {
            try {
                assert client != null;
                client.close();
            } catch (PulsarClientException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Bean
    public PulsarClient basicClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(DEFAULT_SERVICE_URL)
                .authentication(AuthenticationFactory.token(DEFAULT_AUTH_PARAM))
                .build();
    }

    /*@Bean
    public PulsarAdmin adminClient() throws Exception{
        System.out.println("Creating admin client");
        return PulsarAdmin.builder().serviceHttpUrl(PRIMARY_WEB_SERVICE_URL).authentication(AuthenticationFactory.token(PRIMARY_PULSAR_TOKEN)).build();
    }*/

    @Bean
    public String name() {
        return "";
    }

}
