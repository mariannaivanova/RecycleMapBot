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
         tstzrange(current_timestamp, NULL), true);
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
        SET valid_range = tstzrange(lower(valid_range), current_timestamp)
        WHERE valid_range @> current_timestamp AND point_id = OLD.id;

        INSERT INTO points_history
        (point_id, geom, address, title, url, restricted, valid_range, updated)
        VALUES
            (NEW.id, NEW.geom, NEW.address, NEW.title, NEW.url, NEW.restricted,
             tstzrange(current_timestamp, NULL), true);
        RETURN NEW;
    ELSE UPDATE points_history
         SET valid_range = tstzrange(current_timestamp, NULL), updated = false
         WHERE upper(valid_range) is NULL AND point_id = OLD.id;
    END IF;
    RETURN OLD;
END;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER points_update_trigger
    AFTER UPDATE ON points
    FOR EACH ROW EXECUTE PROCEDURE points_update();
