DROP SCHEMA IF EXISTS org CASCADE;
CREATE SCHEMA IF NOT EXISTS org;

CREATE TABLE IF NOT EXISTS org.kueb (
    org TEXT,
    name TEXT,
    profile TEXT,
    status TEXT,
    host TEXT,
    dns TEXT,
    ip4 TEXT,
    ip6 TEXT,
    PRIMARY KEY (org, name)
);