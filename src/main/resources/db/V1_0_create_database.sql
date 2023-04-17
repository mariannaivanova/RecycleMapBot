CREATE TABLE if not exists subcribes
(
    id            BIGINT                NOT NULL PRIMARY KEY,
    chat_id       BIGINT,
    geom     geometry(POINT, 4326) NOT NULL
);