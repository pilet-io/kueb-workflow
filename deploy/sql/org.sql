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
    org TEXT REFERENCES iam.org ON DELETE CASCADE,
    name TEXT,
    profile TEXT,
    status TEXT,
    host TEXT REFERENCES org.host,
    dns TEXT,
    ip4 TEXT,
    ip6 TEXT,
    PRIMARY KEY (org, name)
);

CREATE TABLE IF NOT EXISTS org.kueb_attributes (
    org TEXT,
    name TEXT,
    key TEXT,
    value TEXT,
    FOREIGN KEY (org, name) REFERENCES org.kueb (org, name) ON DELETE CASCADE,
    PRIMARY KEY (org, name, key)
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
    org TEXT,
    name TEXT,
    app TEXT,
    version TEXT,
    status TEXT,
    attributes JSON,
    PRIMARY KEY (org, name, app),
    FOREIGN KEY (org, name) REFERENCES org.kueb (org, name) ON DELETE CASCADE,
    FOREIGN KEY (app, version) REFERENCES org.app_version (app, version)
);


CREATE OR REPLACE VIEW org.view_apps_consolidated AS
SELECT org, name, json_agg(app.*) AS apps
FROM org.app
GROUP BY org, name;

CREATE OR REPLACE VIEW org.view_kueb_attributes AS
SELECT org, name, json_object_agg(key,value) AS attributes
FROM org.kueb_attributes
GROUP BY org, name;


CREATE OR REPLACE VIEW org.view_kueb_full AS
    SELECT kueb.org, kueb.name, kueb.profile, kueb.status, kueb.host, kueb.dns, kueb.ip4, kueb.ip6,
           a.apps, b.attributes
    FROM org.kueb
    LEFT JOIN org.view_apps_consolidated a ON kueb.org = a.org AND kueb.name = a.name
    LEFT JOIN org.view_kueb_attributes b ON kueb.org = b.org AND kueb.name = b.name;




CREATE OR REPLACE TRIGGER kueb_notification_trigger
AFTER INSERT OR UPDATE OR DELETE ON org.kueb FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();

CREATE OR REPLACE TRIGGER app_notification_trigger
AFTER INSERT OR UPDATE OR DELETE ON org.app FOR EACH ROW EXECUTE PROCEDURE fn.send_notification();