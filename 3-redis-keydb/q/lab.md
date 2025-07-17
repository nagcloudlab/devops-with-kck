
docker network create keydb-net

docker run -d \
  --name keydb-node1 \
  --network keydb-net \
  -p 6379:6379 \
  -v /Users/nag/devops-with-kck/3-redis-keydb/q/keydb-node1/keydb.conf:/etc/keydb/keydb.conf \
  eqalpha/keydb keydb-server /etc/keydb/keydb.conf


docker run -d \
  --name keydb-node2 \
  --network keydb-net \
  -p 6380:6379 \
  -v /Users/nag/devops-with-kck/3-redis-keydb/q/keydb-node2/keydb.conf:/etc/keydb/keydb.conf \
  eqalpha/keydb keydb-server /etc/keydb/keydb.conf



This replica sending error to its master, Protocol error, Unbalanced  qoutes in request
after processing the command rreplay