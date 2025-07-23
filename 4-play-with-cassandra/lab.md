

```bash
docker exec -it cassandra1 nodetool status
docker exec -it cassandra1 nodetool ring
docker exec -it cassandra1 cqlsh
describe keyspaces;
create keyspace lab2 with replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
use lab2;


# query : get users by city

create table users_by_city (
    city text,
    name text,
    PRIMARY KEY ((city), name)
);

# insert data
insert into users_by_city (city, name) values ('New York', 'Alice');
insert into users_by_city (city, name) values ('New York', 'Bob');
insert into users_by_city (city, name) values ('Los Angeles', 'Charlie');
insert into users_by_city (city, name) values ('Los Angeles', 'David');
insert into users_by_city (city, name) values ('Chicago', 'Eve');
insert into users_by_city (city, name) values ('Hyderabad', 'Nag');

# query data
select * from users_by_city where city = 'New York';


```