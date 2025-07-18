#!/bin/bash

# Configurable
TARGET_HOST=localhost
TARGET_PORT=6379
REPLICA_CONTAINER=keydb-node2

echo "📤 Sending 5000 keys to master ($TARGET_HOST:$TARGET_PORT)..."
for i in {1..5000}; do
  redis-cli -h $TARGET_HOST -p $TARGET_PORT SET key$i "value$i" > /dev/null
done

echo "🛑 Killing replica $REPLICA_CONTAINER mid-replication..."
docker kill -s SIGKILL $REPLICA_CONTAINER
sleep 3

echo "💣 Injecting malformed Redis command to master ($TARGET_HOST:$TARGET_PORT)..."
echo -e '*3\r\n$3\r\nSET\r\n$3\r\nfoo\r\n$11\r\n"badquoted\r\n' | nc -q 1 $TARGET_HOST $TARGET_PORT
sleep 2

echo "🚀 Restarting replica $REPLICA_CONTAINER..."
docker start $REPLICA_CONTAINER
sleep 5

echo "🔍 Monitoring logs from $REPLICA_CONTAINER (use Ctrl+C to exit)..."
docker logs -f $REPLICA_CONTAINER
