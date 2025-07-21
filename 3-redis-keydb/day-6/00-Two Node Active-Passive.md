

```yaml
version: "3.8"
services:
  keydb-master:
    image: eqalpha/keydb
    container_name: keydb-master
    ports:
      - "6379:6379"
    command: >
      keydb-server
      --requirepass mypass

  keydb-replica:
    image: eqalpha/keydb
    container_name: keydb-replica
    ports:
      - "6380:6379"
    command: >
      keydb-server
      --replicaof keydb-master 6379
      --requirepass mypass
      --masterauth mypass
      --server-stale-data no
```