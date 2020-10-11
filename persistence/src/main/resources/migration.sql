-- feature/bajaLogica
ALTER TABLE USERS ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE POSTS ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE COMMENTS ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- feature/ProfilePic
ALTER TABLE USERS ADD COLUMN avatar_id INTEGER DEFAULT NULL;
ALTER TABLE USERS ADD CONSTRAINT fk_user_avatar FOREIGN KEY (avatar_id) REFERENCES IMAGES (image_id);

--feature/description
ALTER TABLE USERS ADD COLUMN description VARCHAR(400) NOT NULL DEFAULT '';

--feature/likes-dislikes
ALTER TABLE posts_likes ADD COLUMN value INT NOT NULL default 1;
ALTER TABLE comments_likes ADD COLUMN value INT NOT NULL default 1;

-- feature/searchEngine
update movie_categories set name = 'action' where tmdb_category_id = 28;
update movie_categories set name = 'adventure' where tmdb_category_id = 12;
update movie_categories set name = 'animation' where tmdb_category_id = 16;
update movie_categories set name = 'comedy' where tmdb_category_id = 35;
update movie_categories set name = 'crime' where tmdb_category_id = 80;
update movie_categories set name = 'documentary' where tmdb_category_id = 99;
update movie_categories set name = 'drama' where tmdb_category_id = 18;
update movie_categories set name = 'family' where tmdb_category_id = 10751;
update movie_categories set name = 'fantasy' where tmdb_category_id = 14;
update movie_categories set name = 'history' where tmdb_category_id = 36;
update movie_categories set name = 'horror' where tmdb_category_id = 27;
update movie_categories set name = 'music' where tmdb_category_id = 10402;
update movie_categories set name = 'mystery' where tmdb_category_id = 9648;
update movie_categories set name = 'romance' where tmdb_category_id = 10749;
update movie_categories set name = 'scienceFiction' where tmdb_category_id = 878;
update movie_categories set name = 'tvMovie' where tmdb_category_id = 10770;
update movie_categories set name = 'thriller' where tmdb_category_id = 53;
update movie_categories set name = 'war' where tmdb_category_id = 10752;
update movie_categories set name = 'western' where tmdb_category_id = 37;

-- Description is now NOT NULL