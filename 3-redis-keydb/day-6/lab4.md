

docker-compose -f keydb-cluster.yaml up -d

docker exec -it keydb-node1 keydb-cli --cluster create \
  172.18.0.11:6379 \
  172.18.0.12:6379 \
  172.18.0.13:6379 \
  172.18.0.14:6379 \
  172.18.0.15:6379 \
  172.18.0.16:6379 \
  --cluster-replicas 1


docker exec -it keydb-node1 keydb-cli -c cluster info

keydb-cli -c -p 6379

SET user:1000 "hello"
GET user:1000

keydb-cli -c -p 6380
SET city "chennai"
GET city
