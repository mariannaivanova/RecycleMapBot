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
    url VARCHAR
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
    FOREIGN KEY (point_id) REFERENCES points (id),
    FOREIGN KEY (fraction_id) REFERENCES fractions (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscribes_points
(
    subscribe_id INTEGER,
    point_id        INTEGER,
    FOREIGN KEY (subscribe_id) REFERENCES subscribes (id),
    FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE
);

