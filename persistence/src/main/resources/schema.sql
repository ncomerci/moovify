CREATE TABLE IF NOT EXISTS POSTS
(
    post_id       SERIAL PRIMARY KEY,
    creation_date TIMESTAMP   NOT NULL,
    title         VARCHAR(50) NOT NULL,
    email         VARCHAR(40) NOT NULL,
    word_count    INTEGER,
    body          VARCHAR
);


CREATE TABLE IF NOT EXISTS TAGS
(
    post_id integer,
    tag     VARCHAR(30) NOT NULL,
    PRIMARY KEY (post_id, tag),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id)
);


CREATE TABLE IF NOT EXISTS MOVIES
(
    movie_id      SERIAL PRIMARY KEY,
    creation_date TIMESTAMP   NOT NULL,
    premier_date  DATE        NOT NULL,
    title         VARCHAR(50) NOT NULL
);



CREATE TABLE IF NOT EXISTS POST_MOVIE
(
    post_id  integer,
    movie_id integer,
    PRIMARY KEY (post_id, movie_id),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id),
    FOREIGN KEY (movie_id) REFERENCES MOVIES (movie_id)
);


CREATE TABLE IF NOT EXISTS COMMENTS
(
    comment_id    SERIAL PRIMARY KEY,
    parent_id     INT,
    post_id       INT          NOT NULL,
    user_email    VARCHAR(320) NOT NULL,
    creation_date TIMESTAMP    NOT NULL,
    body          VARCHAR      NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES COMMENTS (comment_id),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id)
);

