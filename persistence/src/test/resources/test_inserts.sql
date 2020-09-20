INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (1, '2020-09-09 15:16:29.216000', '2006-06-29', 'Cars');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (3, '2020-09-09 15:22:09.454000', '1992-07-09', 'The beauty and the beast');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (4, '2020-09-09 15:22:56.976000', '1996-03-14', 'Toy Story');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (5, '2020-09-09 15:23:34.854000', '2014-11-06', 'Interestellar');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (2, '2020-09-09 15:17:34.245000', '1989-12-07', 'The little Mermaid');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (6, '2020-09-09 15:25:24.267000', '2004-06-17', 'Shrek 2');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (7, '2020-09-09 15:30:36.074000', '2022-06-20', 'Protocols history');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (8, '2020-09-09 15:17:34.245000', '1989-12-07', 'MovieTest');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (9, '2020-09-09 15:25:24.267000', '2004-06-17', 'Movie_Test_2');
INSERT INTO public.movies (movie_id, creation_date, premier_date, title) VALUES (10, '2020-09-09 15:30:36.074000', '2022-06-20', 'Movie_Test_3');

INSERT INTO ROLES (role_id, role) VALUES (1, 'ROLE_USER');
INSERT INTO ROLES (role_id, role) VALUES (2, 'ROLE_ADMIN');

INSERT INTO public.users (user_id, creation_date, username, password, name, email) VALUES (1, now(), 'userTest1', 'pass', 'name', 'abc@test.com');

INSERT INTO public.user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO POST_CATEGORY (category_id, creation_date, name) VALUES (1, now(), 'watchlist');
INSERT INTO POST_CATEGORY (category_id, creation_date, name) VALUES (2, now(), 'critique');
INSERT INTO POST_CATEGORY (category_id, creation_date, name) VALUES (3, now(), 'news');
INSERT INTO POST_CATEGORY (category_id, creation_date, name) VALUES (4, now(), 'debate');

INSERT INTO public.posts (post_id, creation_date, title, user_id, category_id, word_count, body) VALUES (1, '2020-09-09 15:26:31.440000', 'Post Test 1', 1, 1, 554, 'body_test');
INSERT INTO public.posts (post_id, creation_date, title, user_id, category_id, word_count, body) VALUES (2, '2020-09-09 15:32:57.415000', 'Post Test 2', 1, 1, 2886, 'body_test');
INSERT INTO public.posts (post_id, creation_date, title, user_id, category_id, word_count, body) VALUES (3, '2020-09-09 15:49:38.898000', 'Post Test 3', 1, 1, 932, 'body_test');

INSERT INTO public.post_movie (post_id, movie_id) VALUES (1, 8);
INSERT INTO public.post_movie (post_id, movie_id) VALUES (1, 9);
INSERT INTO public.post_movie (post_id, movie_id) VALUES (3, 10);

INSERT INTO public.tags (post_id, tag) VALUES (3, 'Lorem Ipsum');
INSERT INTO public.tags (post_id, tag) VALUES (1, 'Lorem Ipsum');
INSERT INTO public.tags (post_id, tag) VALUES (1, 'Review');
INSERT INTO public.tags (post_id, tag) VALUES (2, 'Documental');
INSERT INTO public.tags (post_id, tag) VALUES (2, 'Premiere');
INSERT INTO public.tags (post_id, tag) VALUES (3, 'Review');

INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (1, null, 1, 1, '2020-09-09 15:54:42.389000', 'Comment 1');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (2, null, 1, 1, '2020-09-09 15:55:09.674000', 'Comment 2');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (3, 1, 1, 1, '2020-09-09 15:56:00.109000', 'Comment 3');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (4, 1, 1, 1, '2020-09-09 15:56:28.282000', 'Comment 4');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (5, 3, 1, 1, '2020-09-09 15:57:03.564000', 'Comment 5');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (6, 5, 1, 1, '2020-09-09 15:57:28.040000', 'Comment 6');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (7, null, 3, 1, '2020-09-09 16:00:44.957000', 'Comment 7');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (8, null, 3, 1, '2020-09-09 16:01:12.985000', 'Comment 8');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (9, 8, 3, 1, '2020-09-09 16:01:57.509000', 'Comment 9');
INSERT INTO public.comments (comment_id, parent_id, post_id, user_id, creation_date, body) VALUES (10, 8, 3, 1, '2020-09-09 16:02:25.878000', 'Comment 10');