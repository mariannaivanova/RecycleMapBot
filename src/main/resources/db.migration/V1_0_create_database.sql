CREATE TABLE if not exists users
(
    chat_id           BIGINT                NOT NULL PRIMARY KEY,
    first_name VARCHAR,
    last_name VARCHAR,
    user_name VARCHAR
);


CREATE TABLE if not exists subscribes
(
    id            SERIAL          PRIMARY KEY,
    chat_id       BIGINT,
    geom     geometry(POINT, 4326) NOT NULL,
    dist           DOUBLE PRECISION,
    FOREIGN KEY (chat_id) REFERENCES users (chat_id)
);

CREATE TABLE IF NOT EXISTS points
(
    id  BIGINT                NOT NULL PRIMARY KEY,
    geom     geometry(POINT, 4326),
    address TEXT,
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
    point_id BIGINT,
    fraction_id        INTEGER,
    last_update TIMESTAMP,
    FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE,
    FOREIGN KEY (fraction_id) REFERENCES fractions (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscribes_points
(
    subscribe_id BIGINT,
    point_id        BIGINT,
    FOREIGN KEY (subscribe_id) REFERENCES subscribes (id),
    FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscribes_fractions
(
    subscribe_id BIGINT,
    fraction_id        INTEGER,
    FOREIGN KEY (subscribe_id) REFERENCES subscribes (id),
    FOREIGN KEY (fraction_id) REFERENCES fractions (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS points_idx ON points USING gist(geom);

CREATE VIEW points_fractions_view AS
    SELECT p.id, p.title, p.address, p.geom, p.url, p.restricted, f."name" as fraction
    FROM points p
         LEFT OUTER JOIN points_fractions pf ON pf.point_id = p.id
         LEFT OUTER JOIN fractions f ON f.id = pf.fraction_id;

DROP TABLE IF EXISTS points_history;
CREATE TABLE IF NOT EXISTS points_history (
                                              hid SERIAL PRIMARY KEY,
                                              point_id BIGINT,
                                              geom     geometry(POINT, 4326),
                                              address TEXT,
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
CREATE TABLE if not exists applications
(
    id            SERIAL          PRIMARY KEY,
    chat_id       BIGINT,
    geom     geometry(POINT, 4326),
    title           VARCHAR,
    FOREIGN KEY (chat_id) REFERENCES users (chat_id)
);

CREATE TABLE IF NOT EXISTS applications_fractions
(
    application_id INTEGER,
    fraction_id        INTEGER,
    FOREIGN KEY (application_id) REFERENCES applications (id),
    FOREIGN KEY (fraction_id) REFERENCES fractions (id) ON DELETE CASCADE
);

INSERT INTO points_history
(point_id, geom, address, title, url, restricted, valid_range, updated)
SELECT id, geom, address, title, url, restricted,
       tstzrange(now(), NULL), false
FROM points;


CREATE OR REPLACE FUNCTION points_insert() RETURNS trigger AS
$$
BEGIN
    INSERT INTO points_history
    (point_id, geom, address, title, url, restricted, valid_range, updated)
    VALUES
        (NEW.id, NEW.geom, NEW.address, NEW.title, NEW.url, NEW.restricted,
         tstzrange(current_timestamp, NULL), false);
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER points_insert_trigger
    AFTER INSERT ON points
    FOR EACH ROW EXECUTE PROCEDURE points_insert();


CREATE OR REPLACE FUNCTION points_delete() RETURNS trigger AS
$$
BEGIN
    UPDATE points_history
    SET valid_range = tstzrange(lower(valid_range), current_timestamp), updated = true
    WHERE valid_range @> current_timestamp AND point_id = OLD.id;

    /* DELETE FROM points_fractions where point_id = OLD.id;*/
    RETURN NULL;
END;
$$
    LANGUAGE plpgsql;


CREATE TRIGGER points_delete_trigger
    AFTER DELETE ON points
    FOR EACH ROW EXECUTE PROCEDURE points_delete();

CREATE OR REPLACE FUNCTION points_update() RETURNS trigger AS
$$
BEGIN
    IF NEW.address <> OLD.address OR NEW.title <> OLD.title OR NEW.restricted <> OLD.restricted THEN
        UPDATE points_history
        SET point_id = NEW.id, geom = NEW.geom, address = NEW.address, title = NEW.title, url = NEW.url,
            restricted = NEW.restricted, valid_range = tstzrange(current_timestamp, NULL), updated = true;
        RETURN NEW;
    ELSE UPDATE points_history
         SET valid_range = tstzrange(current_timestamp, NULL)
         WHERE upper(valid_range) is NULL AND point_id = OLD.id;
    END IF;
    RETURN OLD;
END;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER points_update_trigger
    AFTER UPDATE ON points
    FOR EACH ROW EXECUTE PROCEDURE points_update();


