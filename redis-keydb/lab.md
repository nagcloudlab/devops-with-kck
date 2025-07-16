

## instala java 17

```bash
sudo apt install openjdk-17-jdk
```

## build the project

```bash
./mvnw clean package -DskipTests
```

## run the project

```bash
java -jar target/hello-service-0.0.1-SNAPSHOT.jar
```


### Deploy PostgreSQL with Docker
```bash
docker run --name postgres -e POSTGRES_PASSWORD=yourpassword -d -p 5432:5432 postgres
```

## Install Redis

```bash
sudo apt install redis-server
```

## Start Redis

```bash
redis-server ./redis.conf
```



