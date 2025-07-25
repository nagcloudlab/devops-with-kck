docker exec -it cassandra1_dc1 cqlsh

CREATE KEYSPACE essentials WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE essentials;

CREATE TABLE movies (
    movie_id UUID ,
    title TEXT,
    release_year INT,
    PRIMARY KEY ((movie_id))
);

INSERT INTO movies (movie_id, title, release_year) VALUES (uuid(), 'The Shawshank Redemption', 1994);
INSERT INTO movies (movie_id, title, release_year) VALUES (uuid(), 'The Godfather', 1972);

SELECT * FROM movies;


---

CREATE TABLE movies_by_actor(
    actor TEXT,
    release_year INT,
    movie_id UUID,
    title TEXT,
    geners SET<TEXT>,
    rating FLOAT,
    PRIMARY KEY ((actor),release_year,movie_id)
) WITH CLUSTERING ORDER BY (release_year DESC,movie_id ASC);

INSERT INTO movies_by_actor (actor, release_year, movie_id, title, geners, rating) VALUES ('Morgan Freeman', 1994, uuid(), 'The Shawshank Redemption', {'Drama'}, 9.3);
INSERT INTO movies_by_actor (actor, release_year, movie_id, title, geners, rating) VALUES ('Marlon Brando', 1972, uuid(), 'The Godfather', {'Crime', 'Drama'}, 9.2);
INSERT INTO movies_by_actor (actor, release_year, movie_id, title, geners, rating) VALUES ('Al Pacino', 1972, uuid(), 'The Godfather', {'Crime', 'Drama'}, 9.2);

SELECT * FROM movies_by_actor;

// select by actor

SELECT * FROM movies_by_actor WHERE actor = 'Morgan Freeman';

// select by actor and release year

SELECT * FROM movies_by_actor WHERE actor = 'Morgan Freeman' AND release_year = 1994;

// select by rating

SELECT * FROM movies_by_actor WHERE rating > 9.0;

---


// select by actor like  'Morgan'
SELECT * FROM movies_by_actor WHERE actor LIKE 'Morgan%';

// secondary index Ssasi index
CREATE CUSTOM INDEX title ON movies_by_actor (actor) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'CONTAINS'};

SELECT * FROM movies_by_actor WHERE actor LIKE 'Morgan%';



---

Materialized Views

CREATE MATERIALIZED VIEW movies_by_actor_and_rating AS
    SELECT actor, release_year, movie_id, title, geners, rating
    FROM movies_by_actor
    WHERE actor IS NOT NULL AND release_year IS NOT NULL AND movie_id IS NOT NULL AND rating IS NOT NULL
    PRIMARY KEY ((actor),rating,release_year,movie_id)
    WITH CLUSTERING ORDER BY (rating DESC,release_year DESC,movie_id ASC);

SELECT * FROM movies_by_actor_and_rating WHERE actor = 'Morgan Freeman' AND rating=9.3;
