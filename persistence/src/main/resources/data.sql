
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

INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(28, 'Action') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(12, 'Adventure') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(16, 'Animation') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(35, 'Comedy') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(80, 'Crime') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(99, 'Documentary') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(18, 'Drama') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10751, 'Family') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(14, 'Fantasy') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(36, 'History') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(27, 'Horror') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10402, 'Music') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(9648, 'Mystery') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10749, 'Romance') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(878, 'Science Fiction') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10770, 'TV Movie') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(53, 'Thriller') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(10752, 'War') ON CONFLICT DO NOTHING;
INSERT INTO  movie_categories(tmdb_category_id, name) VALUES(37, 'Western') ON CONFLICT DO NOTHING;

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