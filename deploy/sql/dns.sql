DROP SCHEMA IF EXISTS dns CASCADE;
CREATE SCHEMA IF NOT EXISTS dns;

CREATE TABLE IF NOT EXISTS dns.zone (
    zone TEXT PRIMARY KEY,
    id TEXT,
    status TEXT
);

CREATE TABLE IF NOT EXISTS dns.entry (
    id TEXT,
    dns TEXT,
    zone TEXT REFERENCES dns.zone,
    cname TEXT,
    org TEXT,
    name TEXT,
    status TEXT,
    FOREIGN KEY (org, name) REFERENCES org.kueb (org, name) ON DELETE CASCADE,
    PRIMARY KEY (dns, zone)
);

CREATE OR REPLACE TRIGGER dns_entry_notification_trigger
    AFTER INSERT OR UPDATE OR DELETE ON dns.entry FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();
