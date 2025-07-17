
# postgresql, redis, keydb, java 17, spring-boot's hello-service

## Install PostgreSQL
```bash
docker run --name postgres -e POSTGRES_PASSWORD=yourpassword -d -p 5433:5432 postgres
```

## Install redis
```bash
docker run --name redis -d -p 6379:6379 redis
```

## Install keydb
```bash
docker run -d --name keydb -p 6380:6379 eqalpha/keydb:latest
```


## instala java 17
```bash
sudo apt install openjdk-17-jdk
```

## build the project
```bash
cd hello-service
./mvnw clean package -DskipTests
```

## run the project
```bash
java -jar target/hello-service-0.0.1-SNAPSHOT.jar
```

## test the project
```bash
curl -X GET http://localhost:8080/hello
ab -n 1000 -c 10 http://localhost:8080/hello
```

