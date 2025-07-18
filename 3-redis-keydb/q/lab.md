
docker network create keydb-net

docker run -d \
  --name keydb-node1 \
  --network keydb-net \
  -p 6391:6379 \
  -v /Users/nag/devops-with-kck/3-redis-keydb/q/keydb-node1/keydb.conf:/etc/keydb/keydb.conf \
  eqalpha/keydb keydb-server /etc/keydb/keydb.conf


docker run -d \
  --name keydb-node2 \
  --network keydb-net \
  -p 6392:6379 \
  -v /Users/nag/devops-with-kck/3-redis-keydb/q/keydb-node2/keydb.conf:/etc/keydb/keydb.conf \
  eqalpha/keydb:x86_64_v5.1.0 keydb-server /etc/keydb/keydb.conf


docker run -d \
  --name keydb-node3 \
  --network keydb-net \
  -p 6393:6379 \
  -v /Users/nag/devops-with-kck/3-redis-keydb/q/keydb-node3/keydb.conf:/etc/keydb/keydb.conf \
  eqalpha/keydb:x86_64_v5.1.0 keydb-server /etc/keydb/keydb.conf


---------------------------------------------------






---------------------------------------------------

docker run -it --rm \
  --name keydb-replica \
  -v $(pwd)/keydb-replica/replica.conf:/etc/keydb/keydb.conf \
  eqalpha/keydb keydb-server /etc/keydb/keydb.conf
