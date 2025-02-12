package com.demo.adobe.astra.streaming;


import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.AuthenticationFactory;

import java.util.Properties;

public class Test {

    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();
        prop.load(Test.class.getClassLoader().getResourceAsStream("pulsar-config.properties"));

        String url = prop.getProperty("PRIMARY_WEB_SERVICE_URL");
        String token = prop.getProperty("PRIMARY_PULSAR_TOKEN");

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

}
