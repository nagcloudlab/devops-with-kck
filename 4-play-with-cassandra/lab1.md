


<!-- create keyspace ks1, dc1 with RF = 3 and dc2 with RF = 2; -->

docker exec -it cassandra1_dc1  cqlsh
CREATE KEYSPACE ks1 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
DESCRIBE KEYSPACE ks1;

create table ks1.transfer_events (
    event TEXT PRIMARY KEY,
    amount float,
    currency TEXT
);

drop table ks1.transfer_events;


CREATE TABLE ks1.users (
    user_id int,
    name TEXT,
    email TEXT,
    PRIMARY KEY (user_id)
);

CONSISTENCY ALL;
INSERT INTO ks1.users (user_id, name, email) VALUES (1, 'Alice', 'alice@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (2, 'Bob', 'bob@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (3, 'Charlie', 'charlie@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (4, 'David', 'david@mail.com');
INSERT INTO ks1.users (user_id, name, email) VALUES (5, 'Eve', 'eve@mail.com');


CONSISTENCY ALL;
SELECT * FROM ks1.users whERE user_id = 5;

-----------------------------
# SRE tasks
-----------------------------

https://nagcloudlab.notion.site/SRE-tasks-2395bab9bf87802f858de750f980a4c8?source=copy_link