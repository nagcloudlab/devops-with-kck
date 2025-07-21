

# KeyDB Active-Active Demo

## This demo sets up two KeyDB nodes in an active-active configuration using Docker Compose. 

```yaml
version: "3.8"
services:
  keydb-node1:
    image: eqalpha/keydb-with-iptables
    container_name: keydb-node1
    ports:
      - "6379:6379"
    command: >
      keydb-server
      --multi-master yes
      --active-replica yes
      --replicaof keydb-node2 6379
      --requirepass mypass
      --masterauth mypass
    networks:
      keydbnet:
        ipv4_address: 172.18.0.2
    cap_add:
      - NET_ADMIN
      - NET_RAW
    user: root

  keydb-node2:
    image: eqalpha/keydb-with-iptables
    container_name: keydb-node2
    ports:
      - "6380:6379"
    command: >
      keydb-server
      --multi-master yes
      --active-replica yes
      --replicaof keydb-node1 6379
      --requirepass mypass
      --masterauth mypass
    networks:
      keydbnet:
        ipv4_address: 172.18.0.3
    cap_add:
      - NET_ADMIN
      - NET_RAW
    user: root

networks:
  keydbnet:
    driver: bridge
    ipam:
      config:
        - subnet: 172.18.0.0/16
```


## Instructions


2. **Start the Docker Compose Services**: Run the following command to start both KeyDB nodes.

```bash
docker-compose up -d
```

3. **Verify the Nodes**: Check if both KeyDB nodes are running and can communicate with each other.

```bash
docker exec keydb-node1 keydb-cli -a mypass info replication
docker exec keydb-node2 keydb-cli -a mypass info replication
```

4. **Confirm the initial State**: Ensure both nodes are in sync and can replicate data.

```bash
docker exec keydb-node1 keydb-cli -a mypass set testkey "Hello from Node 1"
docker exec keydb-node2 keydb-cli -a mypass get testkey
```

