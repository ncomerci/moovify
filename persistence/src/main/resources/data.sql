
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
VALUES ('USER') ON CONFLICT DO NOTHING;

INSERT INTO ROLES (role)
VALUES ('ADMIN') ON CONFLICT DO NOTHING;

INSERT INTO ROLES (role)
VALUES ('NOT_VALIDATED') ON CONFLICT DO NOTHING;

-- Populate Genre --

INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(28, 'action') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(12, 'adventure') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(16, 'animation') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(35, 'comedy') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(80, 'crime') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(99, 'documentary') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(18, 'drama') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10751, 'family') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(14, 'fantasy') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(36, 'history') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(27, 'horror') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10402, 'music') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(9648, 'mystery') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10749, 'romance') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(878, 'scienceFiction') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10770, 'tvMovie') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(53, 'thriller') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10752, 'war') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(37, 'western') ON CONFLICT DO NOTHING;

-- Populate Movies --
-- COPY movies(tmdb_id, imdb_id, original_language, original_title, overview, popularity, release_date, runtime, title, vote_average)
--     FROM '/home/tobias/Documents/ITBA/PAW/paw-2020b-3/persistence/src/main/resources/movies.csv'
--     DELIMITER ','
--     CSV HEADER;
--
-- -- --
-- -- -- -- Populate Genre Movies --
-- COPY movie_to_movie_category(tmdb_category_id, tmdb_id)
--     FROM '/home/tobias/Documents/ITBA/PAW/paw-2020b-3/persistence/src/main/resources/genre_movie.csv'
--     DELIMITER ','
--     CSV HEADER;

-- Path Tobi --
-- /home/tobias/Documents/ITBA/PAW/paw-2020b-3/persistence/src/main/resources/