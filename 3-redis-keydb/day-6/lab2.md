# 1. Start Master-Replica Setup
docker-compose -f keydb-master-replica.yaml up -d

# 2. Connect to the master and set a key
redis-cli -h 127.0.0.1 -p 6379
# In redis-cli prompt:
# set role "master"
# get role
# exit

# 3. Connect to the replica and verify replication
redis-cli -h 127.0.0.1 -p 6380
# In redis-cli prompt:
# get role
# (Should show "master")
# exit

# 4. Try writing to replica (should fail)
redis-cli -h 127.0.0.1 -p 6380
# In redis-cli prompt:
# set test "on-replica"
# (Should error: READONLY You can't write against a read only replica.)
# exit

# 5. Simulate master failure
docker stop keydb-master

# 6. Try writing to replica again (still fails)
redis-cli -h 127.0.0.1 -p 6380
# In redis-cli prompt:
# set failover "still-readonly"
# exit

# 7. Promote replica to master (manual failover)
redis-cli -h 127.0.0.1 -p 6380
# In redis-cli prompt:
# replicaof no one
# set promoted "yes"
# get promoted
# exit

# 8. Restart the original master (observe roles)
docker start keydb-master
# (You can now reconnect to both and discuss split-brain/role reconfig)



-----------------------------