DROP SCHEMA IF EXISTS workflow CASCADE;
CREATE SCHEMA IF NOT EXISTS workflow;

CREATE TABLE IF NOT EXISTS workflow.definition (
    name TEXT,
    description TEXT,
    args JSON,
    run JSON,
    yaml TEXT,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS workflow.run (
    trace TEXT PRIMARY KEY,
    org TEXT,
    name TEXT REFERENCES workflow.definition,
    status TEXT,
    args JSON,
    created BIGINT,
    updated BIGINT
);

CREATE TABLE IF NOT EXISTS workflow.step (
    trace TEXT REFERENCES workflow.run (trace),
    span TEXT,
    status TEXT,
    name TEXT,
    params JSON,
    result INT,
    data JSON,
    created BIGINT,
    PRIMARY KEY (trace, span)
);
CREATE INDEX IF NOT EXISTS idx_step_trace ON workflow.step (trace);

CREATE FUNCTION workflow.trigger_step()
    RETURNS trigger AS $$
BEGIN
    UPDATE workflow.run SET status = NEW.status, updated = NEW.created WHERE trace = NEW.trace;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER my_trigger
    BEFORE INSERT ON workflow.step
    FOR EACH ROW
EXECUTE PROCEDURE workflow.trigger_step();

CREATE TABLE IF NOT EXISTS workflow.log (
    trace TEXT,
    span TEXT,
    command TEXT,
    number INTEGER,
    line TEXT,
    FOREIGN KEY (trace, span) REFERENCES workflow.step (trace, span)
);
CREATE INDEX IF NOT EXISTS idx_log_trace ON workflow.log (trace, span);
