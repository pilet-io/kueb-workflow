-- DROP SCHEMA IF EXISTS iam CASCADE;
CREATE SCHEMA IF NOT EXISTS iam;

CREATE TABLE IF NOT EXISTS iam.role (
    role TEXT PRIMARY KEY
);
INSERT INTO iam.role (role) VALUES ('power_user'), ('ui_user') ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS iam.permissions (
    url TEXT,
    role TEXT REFERENCES iam.role,
    permissions TEXT[],
    PRIMARY KEY (url, role)
);
INSERT INTO iam.permissions (url, role, permissions)
VALUES ('/v1/entity/kuebs', 'power_user', '{GET,POST,PUT,DELETE}'),
       ('/v1/workflow/definitions', 'power_user', '{GET,POST,PUT,DELETE}'),
       ('/v1/workflow/runs', 'power_user', '{GET,POST,PUT,DELETE}'),
       ('/v1/iam/users', 'ui_user', '{GET}') ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS iam.org (
    org TEXT PRIMARY KEY
);
INSERT INTO iam.org (org) VALUES ('pilet'), ('system') ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS iam.users (
    email TEXT PRIMARY KEY,
    name TEXT,
    org TEXT REFERENCES iam.org,
    token TEXT
);
INSERT INTO iam.users (email, name, org, token)
VALUES ('alex.potapov.ru@gmail.com', 'Alex Potapov', 'pilet', '00d5daac356949049dc84d174d9fd20d'),
       ('admin@pilet.io', 'Alex Potapov', 'system', '54b53072540eeeb8f8e9343e71f28176')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS iam.user_roles (
    email TEXT REFERENCES iam.users,
    role TEXT REFERENCES iam.role,
    PRIMARY KEY (email, role)
);
INSERT INTO iam.user_roles (email, role)
VALUES ('alex.potapov.ru@gmail.com', 'power_user'),
       ('admin@pilet.io', 'ui_user')
ON CONFLICT DO NOTHING;

CREATE OR REPLACE VIEW iam.token_url AS
    SELECT token,url,permissions FROM iam.users u
        JOIN iam.user_roles ur on u.email = ur.email
        JOIN iam.permissions p on ur.role = p.role;
