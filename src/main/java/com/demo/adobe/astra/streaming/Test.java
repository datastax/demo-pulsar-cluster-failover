package com.demo.adobe.astra.streaming;


import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

import java.util.Properties;

public class Test {

    static String url;
    static String token;
    static Properties prop;

    public static void main(String[] args) throws Exception {

        prop = new Properties();
        prop.load(Test.class.getClassLoader().getResourceAsStream("pulsar-config.properties"));

        url = prop.getProperty("DEFAULT_SERVICE_URL");
        token = prop.getProperty("DEFAULT_AUTH_PARAM");

        //adminTest();
        clientTest();

    }

    public static void adminTest() throws Exception {
        PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(url)
                .authentication(AuthenticationFactory.token(token))
                .build();

        System.out.println(admin.topics().getPartitionedTopicList(prop.getProperty("TENANT")+"/"+prop.getProperty("NAMESPACE")));
        System.out.println(admin.topics().getReplicationClusters(
                admin.topics().getPartitionedTopicList(prop.getProperty("TENANT")+"/"+prop.getProperty("NAMESPACE")).get(0),
                true
        ));
        System.out.println(admin.topics().getReplicatedSubscriptionStatus(
                admin.topics().getPartitionedTopicList(prop.getProperty("TENANT")+"/"+prop.getProperty("NAMESPACE")).get(0),
                admin.topics().getSubscriptions(
                        admin.topics().getPartitionedTopicList(prop.getProperty("TENANT")+"/"+prop.getProperty("NAMESPACE")).get(0)).get(0)
        ));
        admin.close();
    }

    public static void clientTest() throws Exception {

        PulsarClient client = PulsarClient.builder()
                .serviceUrl(url)
                .authentication(AuthenticationFactory.token(token))
                .build();

        Producer<byte[]> producer = client.newProducer()
                .topic("persistent://"+prop.getProperty("TENANT")+"/"+prop.getProperty("NAMESPACE")+"/"+prop.getProperty("TOPIC"))
                .create();

        System.out.println("producer created");


        producer.close();
        client.close();
    }

}
