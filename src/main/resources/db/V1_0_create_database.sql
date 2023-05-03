CREATE TABLE if not exists users
(
    chat_id           BIGINT                NOT NULL PRIMARY KEY,
    first_name VARCHAR,
    last_name VARCHAR,
    user_name VARCHAR
);


CREATE TABLE if not exists subscribes
(
    id            BIGINT                NOT NULL PRIMARY KEY,
    chat_id       BIGINT,
    geom     geometry(POINT, 4326) NOT NULL,
    dist           DOUBLE PRECISION,
    FOREIGN KEY (chat_id) REFERENCES users (chat_id)
);

CREATE TABLE IF NOT EXISTS points
(
    id  BIGINT                NOT NULL PRIMARY KEY,
    geom     geometry(POINT, 4326),
    address VARCHAR,
    title VARCHAR,
    url VARCHAR,
    restricted BOOLEAN
);

CREATE TABLE IF NOT EXISTS fractions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR
);


CREATE TABLE IF NOT EXISTS points_fractions
(
    point_id INTEGER,
    fraction_id        INTEGER,
    FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE,
    FOREIGN KEY (fraction_id) REFERENCES fractions (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscribes_points
(
    subscribe_id INTEGER,
    point_id        INTEGER,
    FOREIGN KEY (subscribe_id) REFERENCES subscribes (id),
    FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscribes_fractions
(
    subscribe_id INTEGER,
    fraction_id        INTEGER,
    FOREIGN KEY (subscribe_id) REFERENCES subscribes (id),
    FOREIGN KEY (fraction_id) REFERENCES fractions (id) ON DELETE CASCADE
);
/*
CREATE INDEX IF NOT EXISTS points_idx ON points USING gist(geom);

CREATE VIEW points_fractions_view AS
    SELECT p.id, p.title, p.address, p.geom, p.url, f."name" as fraction
    FROM points p
         LEFT OUTER JOIN points_fractions pf ON pf.point_id = p.id
         LEFT OUTER JOIN fractions f ON f.id = pf.fraction_id;*/

DROP TABLE IF EXISTS points_history;
CREATE TABLE IF NOT EXISTS points_history (
                                              hid SERIAL PRIMARY KEY,
                                              point_id BIGINT,
                                              geom     geometry(POINT, 4326),
                                              address VARCHAR,
                                              title VARCHAR,
                                              url VARCHAR,
                                              restricted BOOLEAN,
                                              valid_range TSTZRANGE,
                                              updated BOOLEAN
);

/*CREATE INDEX nyc_streets_history_geom_x
    ON points_history USING GIST (geom);

CREATE INDEX nyc_streets_history_tstz_x
    ON points_history USING GIST (valid_range);*/


