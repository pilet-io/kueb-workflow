DROP SCHEMA IF EXISTS billing CASCADE;
CREATE SCHEMA IF NOT EXISTS billing;

CREATE TABLE IF NOT EXISTS billing.product (
    name TEXT,
    id TEXT PRIMARY KEY,
    debit INTEGER,
    credit INTEGER
);

INSERT INTO billing.product
    VALUES ('100 Tokens', '100-tokens',  100, 0),
           ('50 Tokens', '50-tokens', 50, 0),
           ('Kueb Medium', 'kueb-medium', 0, 1);

CREATE TABLE IF NOT EXISTS billing.daily (
    org TEXT REFERENCES iam.org,
    kueb TEXT,
    product TEXT REFERENCES billing.product,
    day DATE default now(),
    PRIMARY KEY (org,kueb,product,day)
);

CREATE OR REPLACE FUNCTION billing.kueb_created() RETURNS TRIGGER as $$
BEGIN
    IF NEW.status = 'DONE' THEN
        INSERT INTO billing.daily (org, kueb, product)
            VALUES (NEW.org, NEW.name, NEW.product) ON CONFLICT DO NOTHING;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;





