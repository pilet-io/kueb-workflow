DROP SCHEMA IF EXISTS system CASCADE;
CREATE SCHEMA IF NOT EXISTS system;

CREATE TABLE IF NOT EXISTS system.host (
    host TEXT PRIMARY KEY,
    cpu INTEGER,
    ram INTEGER,
    disk INTEGER
);

INSERT INTO system.host (host, cpu, ram, disk)
    VALUES ('fs01.kueb.io', 32, 128, 3000),
           ('fs02.kueb.io', 32, 128, 3000),
           ('fs03.kueb.io', 32, 128, 3000),
           ('fs04.kueb.io', 32, 128, 3000);

CREATE TABLE IF NOT EXISTS system.settings (
    key TEXT PRIMARY KEY,
    category TEXT,
    value TEXT
);

INSERT INTO system.settings (key, category, value)
    VALUES ('fs_root', 'system', '/');