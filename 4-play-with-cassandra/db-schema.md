

# deploy postgres and cassandra containers
# using docker-compose

```bash
docker run -d --name postgres -e POSTGRES_PASSWORD=postgres -p 5433:5432 postgres
docker exec -it postgres psql -U postgres
```

# Postgres
## Relational Data Model Schema
```sql
CREATE TABLE IF NOT EXISTS accounts (
    number varchar(20) PRIMARY KEY,
    balance decimal(10, 2) NOT NULL,
    account_type varchar(20) NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO accounts (number, balance, account_type) VALUES
('1234567890', 1000.00, 'savings'),
('0987654321', 2000.00, 'checking');
```


```bash
docker run -d --name cassandra -p 9042:9042 cassandra
docker exec -it cassandra cqlsh
```

# Cassandra

## Cassandra data Model Schema
```cql
CREATE KEYSPACE IF NOT EXISTS banking WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};
CREATE TABLE IF NOT EXISTS banking.transactions (
    id uuid,
    account_number varchar,
    amount decimal,
    transaction_type varchar,
    created_at timestamp,
    PRIMARY KEY ((id), account_number)
);

```