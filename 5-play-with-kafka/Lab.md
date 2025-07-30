
------------------------------------------
Lab: Kafka Cluster Setup
------------------------------------------

download kafka & unzip & copy to 3 folders
```bash
wget https://dlcdn.apache.org/kafka/3.9.1/kafka_2.13-3.9.1.tgz
tar -xzf kafka_2.13-3.9.1.tgz
mv kafka_2.13-3.9.1 kafka1
cp -rf kafka1 kafka2
cp -rf kafka1 kafka3
```

start zookeeper
```bash
kafka1/bin/zookeeper-server-start.sh kafka1/config/zookeeper.properties
```


start kafka server1
```bash
kafka1/bin/kafka-server-start.sh kafka1/config/server.properties
```

start kafka server2
```bash
kafka2/bin/kafka-server-start.sh kafka2/config/server.properties
```

start kafka server3
```bash
kafka3/bin/kafka-server-start.sh kafka3/config/server.properties
```


kafka-ui
```bash
mkdir kafka-ui
cd kafka-ui
curl -L https://github.com/provectus/kafka-ui/releases/download/v0.7.2/kafka-ui-api-v0.7.2.jar --output kafka-ui-api-v0.7.2.jar
```

nano application.yml

```yaml
kafka:
  clusters:
    - name: local
      bootstrapServers: localhost:9092
```

run kafka-ui
```bash
java -Dspring.config.additional-location=application.yml --add-opens java.rmi/javax.rmi.ssl=ALL-UNNAMED -jar kafka-ui-api-v0.7.2.jar      
```


-----------------------------------------------
Lab : Topic Management
-----------------------------------------------

create topic
```bash
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic1
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic topic1

kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic2 --partitions 2
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic topic2

kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic3 --partitions 3
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic topic3

kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic4 --partitions 40
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic topic4

kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic topic1
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic topic2
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic topic3
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic topic4
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list


kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic topic5 --partitions 3 --replication-factor 3
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic topic5
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic topic5


```



------------------------------------------------
Reset commited offset for specific topic & group
------------------------------------------------

```bash
# to earliest
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-earliest --execute
# to latest
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-latest --execute
# to specific offset
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-offset 10 --execute
# to specific timestamp
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-datetime "2023-10-01T00:00:00" --execute
# to specific date
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-datetime "2023-10-01" --execute
# to specific date and time
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-datetime "2023-10-01T12:00:00" --execute
# to specific date and time with timezone
kafka1/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group g1 --reset-offsets --topic transfer-events --to-datetime "2023-10-01T12:00:00+05:30" --execute
```



------------------------------------------------
Topics
------------------------------------------------

```bash
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic upi-transfer-events --partitions 3 --replication-factor 3
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic neft-transfer-events --partitions 3 --replication-factor 3
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic imps-transfer-events --partitions 3 --replication-factor 3
kafka1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic rtgs-transfer-events --partitions 3 --replication-factor 3
```



--------------------------------------------------
kafka connect cluster
--------------------------------------------------

```bash
kafka1/bin/connect-distributed.sh kafka1/config/connect-distributed-worker1.properties
kafka1/bin/connect-distributed.sh kafka1/config/connect-distributed-worker2.properties
```


get the list of connector plugins
```bash
curl -X GET http://localhost:8083/connector-plugins
```

create cassandra sink connector
```bash
curl -X POST -H "Content-Type: application/json" --data '{
  "name": "cassandra-sink-connector",
  "config": {
    "connector.class": "com.datastax.oss.kafka.sink.CassandraSinkConnector",
    "tasks.max": "1",
    "topics": "transfer-events",
    
    "contactPoints": "localhost",                 
    "port": "9042",                               
    "loadBalancing.localDc": "datacenter1",       

    "keyspace": "ks1",                            

    "topic.transfer-events.ks1.transfer_events.mapping": "transaction_id=value.transaction_id,from_account=value.from_account,to_account=value.to_account,amount=value.amount,currency=value.currency,transfer_type=value.transfer_type,timestamp=value.timestamp,status=value.status,failure_reason=value.failure_reason",

    "topic.transfer-events.ks1.transfer_events.consistencyLevel": "QUORUM",
    "topic.transfer-events.ks1.transfer_events.ttl": "0",

    "maxConcurrentRequests": "500",
    "queryExecutionTimeoutMillis": "10000"
  }
}' http://localhost:8083/connectors

```


get the list of connectors
```bash
curl -X GET http://localhost:8083/connectors
```

get the status of a connector
```bash
curl -X GET http://localhost:8083/connectors/cassandra-sink-connector/status
```

delete a connector
```bash
curl -X DELETE http://localhost:8083/connectors/cassandra-sink-connector
```

------------------------------------------------
Monitoring
------------------------------------------------

JMX Exporter to each Kafka broker to expose metrics
Prometheus to scrape metrics from all brokers
Grafana to visualize
(Optional later) Kafka Lag Exporter

```bash
# Install JMX Exporter
mkdir jmx_exporter
cd jmx_exporter
wget wget https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.17.0/jmx_prometheus_javaagent-0.17.0.jar
# Create a configuration file for JMX Exporter
nano kafka-jmx-config.yaml
```

```yaml
# ~/jmx-exporter/kafka.yml
startDelaySeconds: 0
lowercaseOutputName: true
lowercaseOutputLabelNames: true
rules:
  - pattern: "kafka.server<type=(.+), name=(.+)><>Value"
    name: kafka_$1_$2
    type: GAUGE
```

To configure the JMX Exporter with Kafka, you need to set the `KAFKA_OPTS` environment variable in each Kafka broker's configuration. You can do this by adding the following line to your `kafka-server-start.sh` script or by exporting it in your terminal session before starting the Kafka server.

```bash
export KAFKA_OPTS="-javaagent:/Users/nag/devops-with-kck/5-play-with-kafka/jmx_exporter/jmx_prometheus_javaagent-0.17.0.jar=707X:/Users/nag/devops-with-kck/5-play-with-kafka/jmx_exporter/kafka-jmx-config.yaml"
```

-------------------------------------------------
Kafka Exporter
-------------------------------------------------

```bash
docker run -d \
  -p 9308:9308 \
  -e KAFKA_BROKER=host.docker.internal:9092 \
  danielqsj/kafka-exporter \
  --kafka.server=host.docker.internal:9092 \
  --log.level=info
```
