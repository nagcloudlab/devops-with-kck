
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