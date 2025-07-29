
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