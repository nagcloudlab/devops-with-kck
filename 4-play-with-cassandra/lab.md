

```bash
docker compose up -d
docker exec -it cassandra1 nodetool status
docker exec -it cassandra1 nodetool ring
docker exec -it cassandra1 cqlsh

describe keyspaces;

create keyspace lab1 with replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
use lab1;

create keyspace ks1 with replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
use ks1;


```


