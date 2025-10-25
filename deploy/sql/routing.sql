DROP SCHEMA IF EXISTS routing CASCADE;
CREATE SCHEMA IF NOT EXISTS routing;

CREATE TABLE IF NOT EXISTS routing.zone (
    zone TEXT PRIMARY KEY,
    id TEXT,
    status TEXT
);

CREATE TABLE IF NOT EXISTS routing.entry (
    kueb_id TEXT REFERENCES org.kueb ON DELETE CASCADE ,
    id TEXT,
    dns TEXT,
    zone TEXT REFERENCES routing.zone,
    cname TEXT,
    status TEXT,
    PRIMARY KEY (dns, zone)
);

CREATE TABLE IF NOT EXISTS routing.route (
    kueb_id TEXT REFERENCES org.kueb ON DELETE CASCADE,
    host TEXT,
    entry TEXT, -- https / k8s
    ip4 TEXT,
    port INTEGER,
    tls TEXT,
    dns TEXT,
    zone TEXT,
    status TEXT,
    PRIMARY KEY (kueb_id, entry)
);

CREATE OR REPLACE TRIGGER routing_entry_notification_trigger
    AFTER INSERT OR UPDATE OR DELETE ON routing.entry FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();

CREATE OR REPLACE TRIGGER routing_route_notification_trigger
    AFTER INSERT OR UPDATE OR DELETE ON routing.route FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();

CREATE OR REPLACE TRIGGER routing_entry_status_trigger
    BEFORE INSERT ON routing.entry FOR EACH ROW EXECUTE PROCEDURE fn.status_requested();

CREATE OR REPLACE TRIGGER routing_route_status_trigger
    BEFORE INSERT ON routing.route FOR EACH ROW EXECUTE PROCEDURE fn.status_requested();