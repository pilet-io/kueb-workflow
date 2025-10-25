DROP SCHEMA IF EXISTS fn CASCADE;
CREATE SCHEMA IF NOT EXISTS fn;


CREATE TABLE IF NOT EXISTS fn.trigger_log (
    name TEXT,
    created TIMESTAMP default now(),
    new JSON,
    old JSON
);

CREATE OR REPLACE FUNCTION fn.send_notification() RETURNS TRIGGER as $$
DECLARE
    v_name TEXT;
BEGIN
    v_name := TG_TABLE_SCHEMA || '_' || TG_TABLE_NAME || '_' || TG_OP;
    v_name := LOWER(v_name);
    RAISE NOTICE 'v_name is %', v_name;
    IF (TG_OP = 'DELETE') THEN
        PERFORM pg_notify(v_name,row_to_json(OLD)::text);
    ELSE
        PERFORM pg_notify(v_name,row_to_json(NEW)::text);
    END IF;
    INSERT INTO fn.trigger_log (name, new, old)
        VALUES (v_name, row_to_json(NEW), row_to_json(OLD));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn.status_requested() RETURNS TRIGGER as $$
BEGIN
    IF NEW.status is null THEN
        NEW.status = 'REQUESTED';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
