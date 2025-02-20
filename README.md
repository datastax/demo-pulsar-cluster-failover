# demo-pulsar-cluster-failover
A sample app that demos [Pulsar](https://pulsar.apache.org/ "Pulsar") client-side [Controlled failover](https://pulsar.apache.org/docs/3.3.x/client-libraries-cluster-level-failover/#controlled-failover "Controlled failover") 

This app has three components
- A [**Producer** app](https://github.com/datastax/demo-pulsar-cluster-failover/blob/main/src/main/java/com/demo/adobe/astra/streaming/impl/StreamingProducer.java "**Producer** app") that produce messages a regular intervals. You can start one of more instances of this app.
- A [**Consumer** app](https://github.com/datastax/demo-pulsar-cluster-failover/blob/main/src/main/java/com/demo/adobe/astra/streaming/impl/StreamingConsumer.java "**Consumer** app") that consumes messages produced by the above Producer. You can start one of more instances of this app.
- A [**Provider** app](https://github.com/datastax/demo-provider-cluster-failover/blob/main/src/main/java/com/demo/service/provider/astra/Application.java "**Provider** app") that works as a simple URL Service Provider as [shown here](https://pulsar.apache.org/docs/3.3.x/client-libraries-cluster-level-failover/#controlled-failover "shown here"). You must start only one instance of this app before you start the Producer or Consumer. If you plan to start more than one instance of this app for HA, you must put these instances behind a single HA load balancer.

Below is a high-level diagram of the above components and the Pulsar Controlled Failover flow

<img width="675" src=https://pulsar.apache.org/assets/images/cluster-level-failover-3-e4c1f0e86f1652f300f2bc54d342b955.png  alt="src/main/resources/images/cluster-level-failover.png"/>


------------

## Prerequisites
- Java 11
- Apache Maven 3.8.x (to build the app)
- Pulsar 3.x 
- Two (or more) Pulsar based Streaming clusters
  - One clusters will be the primary, while all the other clusters will be DR clusters that can takeover in case of a failover
  - For the purpose of this demo, we will use [Astra Streaming](https://www.datastax.com/products/astra-streaming) (A SaaS Streaming provider by DataStax) to standup two Pulsar based streaming clusters.
- [Provider](https://github.com/datastax/demo-provider-cluster-failover) application is up and running.
- Streaming cluster properties - Before building/running the application these properties needs to be updated at src/main/resources/pulsar-config.properties
  - Broker/Service URL (<pulsar+ssl://>)
  - Authentication Token
  - Tenant, Namespace, Topic, and Subscription
  - URL provider (<http://localhost:8081/provider>)
 
## Working with the applications
### Building the app
From the root folder of the repo, run the command:
`mvn clean package spring-boot:repackage`

### Running the app
To run the application use command: `java -jar targe/demo-pulsar-cluster-failover-<version>.jar`
- `<version>` - should be updated based on the latest version build of the application. Refer to <version> at [pom.xml](pom.xml)

#### Application run order:
1. SpringBoot application initialization 
2. Application will connect to Provider app to get primary cluster configuration. 
3. Pulsar client will be initialized based on the configuration returned by the Provider app. 
4. Post client initialization, the application will first bring up a producer and then a consumer. 
6. At the end of this you will have the application running with one producer and one consumer.

### Managing Application
The application supports handling for multiple producers and consumers. To achieve that below endpoints will be used:
#### Producer:
- You can bring up additional producers using the endpoint `/startProducer`.
  - If running in local, use the endpoint: `http://localhost:8080/startProducer`
- For more details please refer to how to [work with producer](https://pulsar.apache.org/docs/3.0.x/client-libraries-producers/).
#### Consumer: 
- You can bring up additional consumer using the endpoint `/startConsumer`.
  - If running in local, use the endpoint: `http://localhost:8080/startConsumer`
- For more details please refer to how to [work with consumers](https://pulsar.apache.org/docs/3.0.x/client-libraries-consumers/).
#### Client:
- You can close the client connection using the endpoint `/conn/close`, this will close the client connecting and bringing down any running producers or consumers.
  - If running in local, use the endpoint: `http://localhost:8080/conn/close`
- For more details please refer to how to [work with clients](https://pulsar.apache.org/docs/3.3.x/client-libraries-consumers/).

### Performing failover
Provider app will handle the configuration for the primaryCluster and how to handle failover.
For detailed information on how to perform failover, please refer to the [Provider App](https://github.com/datastax/demo-provider-cluster-failover/blob/main/README.md)