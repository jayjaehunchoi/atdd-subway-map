TRUNCATE TABLE STATION;
TRUNCATE TABLE LINE;
TRUNCATE TABLE LINE_STATION;
ALTER TABLE LINE ALTER COLUMN id RESTART WITH 1;
ALTER TABLE STATION ALTER COLUMN id RESTART WITH 1;