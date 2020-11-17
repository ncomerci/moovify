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

-- hibernateMigration
alter table user_role drop constraint user_role_role_id_fkey;
alter table user_role drop constraint user_role_pkey;
alter table user_role add column role_name varchar(100);
update user_role ur set role_name = (select role from roles r where r.role_id = ur.role_id);
alter table user_role alter column role_name set not null;
alter table user_role drop column role_id;
alter table user_role add constraint user_role_pkey primary key (user_id, role_name);
drop table roles;

alter table posts_likes drop constraint posts_likes_pkey;
alter table posts_likes add column post_likes_id serial primary key;
alter table posts_likes add constraint posts_likes_unique unique (post_id, user_id);

alter table comments_likes drop constraint comments_likes_pkey;
alter table comments_likes add column comments_likes_id serial primary key;
alter table comments_likes add constraint comments_likes_unique unique (comment_id, user_id);

alter table comments_likes add constraint comments_likes_pkey primary key (comments_likes_id);

-- User Locale -- TODO: Add to schema
ALTER TABLE users ADD COLUMN language VARCHAR(15) NOT NULL default 'en';

-- Edit Post --
ALTER TABLE posts ADD COLUMN edited BOOLEAN NOT NULL default false;
ALTER TABLE posts ADD COLUMN last_edited TIMESTAMP default NULL;