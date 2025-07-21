# 1. Start KeyDB in standalone mode
docker-compose -f keydb-standalone.yaml up -d

# 2. Connect and set a key
redis-cli -h 127.0.0.1 -p 6379
# In redis-cli prompt, run:
# set demo "standalone"
# get demo
# (Then type 'exit' to leave the CLI)

# 3. Stop the KeyDB container (simulates server failure)
docker stop keydb-standalone

# 4. Start KeyDB again (recovery attempt)
docker start keydb-standalone

# 5. Reconnect and check if the key still exists
redis-cli -h 127.0.0.1 -p 6379
# In redis-cli prompt, run:
# get demo
