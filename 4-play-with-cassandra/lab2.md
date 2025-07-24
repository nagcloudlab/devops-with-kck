


<!-- create keyspace ks1, dc1 with RF = 3 and dc2 with RF = 2; -->

docker exec -it cassandra1_dc1  cqlsh
CREATE KEYSPACE ks1 WITH REPLICATION = {'class': 'NetworkTopologyStrategy', 'dc1': 3, 'dc2': 2};
DESCRIBE KEYSPACE ks1;

CREATE TABLE ks1.users (
    user_id UUID PRIMARY KEY,
    name TEXT,
    email TEXT
);

INSERT INTO ks1.users (user_id, name, email) VALUES (uuid(), 'Alice', 'alice@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (uuid(), 'Bob', 'bob@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (uuid(), 'Charlie', 'charlie@mail.com');

SELECT * FROM ks1.users;

CONSISTENCY ALL;

INSERT INTO ks1.users (user_id, name, email) VALUES (uuid(), 'Dave', 'dave@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (uuid(), 'Eve', 'eve@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (uuid(), 'Frank', 'frank1@mail.com');


docker exec -it cassandra1_dc1 nodetool getendpoints <keyspace> <table> <partition_key>
docker exec -it cassandra1_dc1 nodetool getendpoints ks1 users <user_id>



SELECT DISTINCT user_id FROM ks1.users;

docker exec -it cassandra1_dc1 nodetool tablestats ks1


-----------------------------
# SRE tasks
-----------------------------

https://nagcloudlab.notion.site/SRE-tasks-2395bab9bf87802f858de750f980a4c8?source=copy_link