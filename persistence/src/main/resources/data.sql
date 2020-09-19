
-- Populate Post Categories --
INSERT INTO POST_CATEGORY (creation_date, name)
VALUES (now(), 'watchlist') ON CONFLICT DO NOTHING;

INSERT INTO POST_CATEGORY (creation_date, name)
VALUES (now(), 'critique') ON CONFLICT DO NOTHING;

INSERT INTO POST_CATEGORY (creation_date, name)
VALUES (now(), 'news') ON CONFLICT DO NOTHING;

INSERT INTO POST_CATEGORY (creation_date, name)
VALUES (now(), 'debate') ON CONFLICT DO NOTHING;

-- Populate User Roles --
INSERT INTO ROLES (role)
VALUES ('ROLE_USER') ON CONFLICT DO NOTHING;

INSERT INTO ROLES (role)
VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;
