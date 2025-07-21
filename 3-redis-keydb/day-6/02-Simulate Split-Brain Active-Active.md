

# KeyDB Split-Brain Active-Active Demo

## This demo sets up two KeyDB nodes in an active-active configuration using Docker Compose. The nodes are configured to communicate with each other, and iptables rules are applied to simulate a split-brain scenario.

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

1. **Build the Docker Image**: Ensure you have the Dockerfile set up correctly to build the KeyDB image with iptables support.

```bash
docker build -t eqalpha/keydb-with-iptables .
```

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

5. **Simulate Split-Brain**: Use iptables to block communication between the two nodes.

```bash
docker exec keydb-node1 iptables -A INPUT -s 172.18.0.3 -j DROP
docker exec keydb-node2 iptables -A INPUT -s 172.18.0.2 -j DROP
```

6. **Verify Split-Brain**: Check the replication status on both nodes to confirm they are no longer communicating.

```bash
docker exec keydb-node1 keydb-cli -a mypass info replication
docker exec keydb-node2 keydb-cli -a mypass info replication

docker exec keydb-node1 keydb-cli -a mypass set testkey "Hello from Node 1 after split"
docker exec keydb-node2 keydb-cli -a mypass set testkey "Hello from Node 2 after split"
```

7. **Resolve Split-Brain**: Remove the iptables rules to restore communication between the nodes.

```bash
docker exec keydb-node1 iptables -D INPUT -s 172.18.0.3 -j DROP
docker exec keydb-node2 iptables -D INPUT -s 172.18.0.2 -j DROP
```

8. **Check Data Consistency**: After restoring communication, check the data on both nodes to see how they handle the split-brain scenario.

```bash
docker exec keydb-node1 keydb-cli -a mypass get testkey
docker exec keydb-node2 keydb-cli -a mypass get testkey
```

## Summary of What Was Demonstrated

Active-active KeyDB multi-master deployment via Docker Compose.
Manual network partitioning using iptables to simulate split-brain.
Independent writes during split (conflict simulation).
Healing the network and observing data divergence.
Understanding that KeyDB does not resolve conflicting changes to the same key post split-brain.
