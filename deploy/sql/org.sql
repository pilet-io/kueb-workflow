DROP SCHEMA IF EXISTS org CASCADE;
CREATE SCHEMA IF NOT EXISTS org;

CREATE TABLE IF NOT EXISTS org.host (
    host TEXT PRIMARY KEY,
    cpu INTEGER,
    ram INTEGER,
    disk INTEGER
);

INSERT INTO org.host (host, cpu, ram, disk)
VALUES ('fs01.kueb.io', 32, 128, 3000),
       ('fs02.kueb.io', 32, 128, 3000),
       ('fs03.kueb.io', 32, 128, 3000),
       ('fs04.kueb.io', 32, 128, 3000)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS org.settings (
    key TEXT PRIMARY KEY,
    category TEXT,
    value TEXT
);

INSERT INTO org.settings (key, category, value)
VALUES ('fs_root', 'system', '/')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS org.kueb (
    id TEXT PRIMARY KEY,
    org TEXT REFERENCES iam.org ON DELETE CASCADE,
    name TEXT,
    profile TEXT,
    product TEXT REFERENCES billing.product,
    status TEXT,
    host TEXT REFERENCES org.host,
    dns TEXT,
    ip4 TEXT,
    ip6 TEXT
);

CREATE TABLE IF NOT EXISTS org.kueb_attributes (
    kueb_id TEXT REFERENCES org.kueb ON DELETE CASCADE,
    name TEXT,
    key TEXT,
    value TEXT,
    PRIMARY KEY (kueb_id, key)
);

CREATE TABLE IF NOT EXISTS org.app_version (
    app TEXT,
    version TEXT,
    preferred TEXT default 'N',
    PRIMARY KEY (app, version)
);

INSERT INTO org.app_version (app, version, preferred)
VALUES ('postgres', '17.5.0', 'Y'),
       ('kafka', '7.2.15', 'Y'),
       ('redis', '8.0.2', 'Y');

CREATE TABLE IF NOT EXISTS org.app (
    id TEXT PRIMARY KEY,
    kueb_id TEXT REFERENCES org.kueb ON DELETE CASCADE,
    app TEXT,
    version TEXT,
    status TEXT,
    attributes JSON,
    FOREIGN KEY (app, version) REFERENCES org.app_version (app, version)
);


CREATE OR REPLACE VIEW org.view_apps_consolidated AS
SELECT kueb_id, json_agg(app.*) AS apps
FROM org.app
GROUP BY kueb_id;

CREATE OR REPLACE VIEW org.view_kueb_attributes AS
SELECT kueb_id, json_object_agg(key,value) AS attributes
FROM org.kueb_attributes
GROUP BY kueb_id;


CREATE OR REPLACE VIEW org.view_kueb_full AS
    SELECT kueb.id, kueb.org, kueb.name, kueb.profile, kueb.status, kueb.host, kueb.dns, kueb.ip4, kueb.ip6,
           a.apps, b.attributes
    FROM org.kueb
    LEFT JOIN org.view_apps_consolidated a ON kueb.id = a.kueb_id
    LEFT JOIN org.view_kueb_attributes b ON kueb.id = a.kueb_id;



CREATE OR REPLACE TRIGGER kueb_notification_trigger
AFTER INSERT OR UPDATE OR DELETE ON org.kueb FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();


CREATE OR REPLACE TRIGGER billing_kueb_created_trigger
AFTER UPDATE ON org.kueb FOR EACH ROW EXECUTE PROCEDURE billing.kueb_created();

CREATE OR REPLACE TRIGGER app_notification_trigger
AFTER INSERT OR UPDATE OR DELETE ON org.app FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();