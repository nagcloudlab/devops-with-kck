import sys
import time
from rediscluster import RedisCluster

# Determine startup nodes based on command-line arguments
if len(sys.argv) != 3:
    startup_nodes = [
        {"host": "127.0.0.1", "port": 7000},
        {"host": "127.0.0.1", "port": 7001},
        {"host": "127.0.0.1", "port": 7002},
        {"host": "127.0.0.1", "port": 7003},
    ]
else:
    startup_nodes = [
        {"host": sys.argv[1], "port": int(sys.argv[2])}
    ]

# Create Redis cluster client
rc = RedisCluster(startup_nodes=startup_nodes, decode_responses=True)

# Initialize the last key value
last = None

while last is None:
    try:
        last = rc.get("__last__")
        if last is None:
            last = 0
    except Exception as e:
        print(f"error {str(e)}")
        time.sleep(1)

# Write to cluster and read from cluster
for x in range(int(last) + 1, 1000001):  # Adjust the range for demonstration purposes
    try:
        rc.set(f"foo{x}", x)
        print(f"Cluster set: foo{x} = {x}")

        # Allow some time for the data to be distributed
        time.sleep(0.1)

        cluster_value = rc.get(f"foo{x}")
        print(f"Cluster read: foo{x} = {cluster_value}")

        rc.set("__last__", x)
    except Exception as e:
        print(f"error {str(e)}")
    # Optional: Uncomment the next line to introduce a delay between operations
    # time.sleep(0.1)

# Clean up by removing the keys from the cluster
for x in range(int(last) + 1):
    rc.delete(f"foo{x}")
rc.delete("__last__")