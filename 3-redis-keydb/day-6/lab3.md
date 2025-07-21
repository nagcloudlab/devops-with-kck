# 1. Start Master-Replica Setup
docker-compose -f keydb-master-replica.yaml up -d

# 2. Set a key on master and verify on replica
redis-cli -h 127.0.0.1 -p 6379 set splitdemo "before split"
redis-cli -h 127.0.0.1 -p 6380 get splitdemo
# Should output: before split

# 3. Simulate network partition: block replica from talking to master
docker exec keydb-master iptables -A INPUT -s 172.18.0.21 -j DROP
docker exec keydb-replica iptables -A INPUT -s 172.18.0.20 -j DROP

# 4. Change the key on master (replica will NOT see the update)
redis-cli -h 127.0.0.1 -p 6379 set splitdemo "updated on master after split"
redis-cli -h 127.0.0.1 -p 6380 get splitdemo
# Should still output: before split

# 5. Optional: try to write on replica (still fails, remains read-only)
redis-cli -h 127.0.0.1 -p 6380 set splitdemo "try write on stale replica"
# Should error: READONLY You can't write against a read only replica.

# 6. Heal the network partition
docker exec keydb-master iptables -D INPUT -s 172.18.0.21 -j DROP
docker exec keydb-replica iptables -D INPUT -s 172.18.0.20 -j DROP

# 7. Wait a moment, then check data on replica (should sync again)
sleep 2
redis-cli -h 127.0.0.1 -p 6380 get splitdemo
# Should now output: updated on master after split

# --- Talking Points ---
# - Replica had stale data during the partition (network split)
# - Writes on master were NOT replicated during network failure
# - Once connectivity is restored, the replica catches up (no data loss, but temporary staleness)
# - If your app reads from replicas, it may see old data until healing



docker exec keydb-mm1 iptables -A INPUT -s 172.18.0.41 -j DROP
docker exec keydb-mm1 iptables -A INPUT -s 172.18.0.42 -j DROP
docker exec keydb-mm2 iptables -A INPUT -s 172.18.0.40 -j DROP
docker exec keydb-mm3 iptables -A INPUT -s 172.18.0.40 -j DROP