CREATE TABLE IF NOT EXISTS IMAGES
(
    image_id        SERIAL          PRIMARY KEY,
    image           BYTEA           NOT NULL,
    security_tag    VARCHAR(100)    NOT NULL
--     type            VARCHAR(40)     default NULL
);

CREATE TABLE IF NOT EXISTS USERS
(
    user_id         SERIAL       PRIMARY KEY,
    creation_date   TIMESTAMP    NOT NULL,
    username        VARCHAR(50)  UNIQUE NOT NULL,
    password        VARCHAR(200) NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    email           VARCHAR(200) UNIQUE NOT NULL,
    avatar_id       INTEGER,
    description     VARCHAR(400) NOT NULL,
    enabled         BOOLEAN      NOT NULL,
    language         VARCHAR(15)  NOT NULL,

    FOREIGN KEY (avatar_id) REFERENCES IMAGES (image_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS USER_VERIFICATION_TOKEN
(
    token_id    SERIAL       PRIMARY KEY,
    user_id     INTEGER      UNIQUE NOT NULL,
    token       TEXT         UNIQUE NOT NULL,
    expiry      TIMESTAMP    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PASSWORD_RESET_TOKEN
(
    token_id    SERIAL       PRIMARY KEY,
    user_id     INTEGER      UNIQUE NOT NULL,
    token       TEXT         UNIQUE NOT NULL,
    expiry      TIMESTAMP    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS USER_ROLE
(
    user_id     INTEGER         NOT NULL,
    role_name   VARCHAR(100)    NOT NULL,
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS POST_CATEGORY
(
    category_id     SERIAL       PRIMARY KEY,
    creation_date   TIMESTAMP    NOT NULL,
    name            VARCHAR(50)  UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS POSTS
(
    post_id       SERIAL       PRIMARY KEY,
    creation_date TIMESTAMP    NOT NULL,
    title         VARCHAR(200) NOT NULL,
    user_id       INTEGER      NOT NULL,
    category_id   INTEGER      NOT NULL,
    word_count    INTEGER      NOT NULL,
    body          TEXT         NOT NULL,
    enabled       BOOLEAN      NOT NULL,
    edited        BOOLEAN      NOT NULL,
    last_edited   TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES POST_CATEGORY (category_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)     REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS POSTS_LIKES
(
    post_likes_id   SERIAL      PRIMARY KEY,
    post_id         INTEGER     NOT NULL,
    user_id         INTEGER     NOT NULL,
    value           INTEGER     NOT NULL,
    UNIQUE(post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TAGS
(
    post_id INTEGER     NOT NULL,
    tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (post_id, tag),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS MOVIE_CATEGORIES
(
    category_id             SERIAL          PRIMARY KEY,
    tmdb_category_id        INTEGER         NOT NULL    UNIQUE,
    name                    VARCHAR(50)     NOT NULL
);

CREATE TABLE IF NOT EXISTS MOVIES
(
    movie_id                SERIAL          PRIMARY KEY,
    creation_date           TIMESTAMP       NOT NULL    DEFAULT now(),
    release_date            DATE            NOT NULL,
    title                   VARCHAR(200)    NOT NULL,
    original_title          VARCHAR(200)    NOT NULL,
    tmdb_id                 INTEGER         NOT NULL    UNIQUE,
    imdb_id                 VARCHAR(20)     NOT NULL    UNIQUE,
    original_language       VARCHAR(10)     NOT NULL,
    overview                TEXT            NOT NULL,
    popularity              FLOAT           NOT NULL,
    runtime                 FLOAT           NOT NULL,
    vote_average            FLOAT           NOT NULL,
    poster_id               INTEGER,
    FOREIGN KEY (poster_id) REFERENCES IMAGES (image_id)

);

CREATE TABLE IF NOT EXISTS MOVIE_TO_MOVIE_CATEGORY
(
    tmdb_category_id    INTEGER    NOT NULL,
    tmdb_id             INTEGER    NOT NULL,
    PRIMARY KEY (tmdb_category_id, tmdb_id),
    FOREIGN KEY (tmdb_category_id) REFERENCES MOVIE_CATEGORIES(tmdb_category_id) ON DELETE CASCADE,
    FOREIGN KEY (tmdb_id) REFERENCES MOVIES (tmdb_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS POST_MOVIE
(
    post_id         INTEGER    NOT NULL,
    movie_id        INTEGER    NOT NULL,
    PRIMARY KEY (post_id, movie_id),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES MOVIES (movie_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS COMMENTS
(
    comment_id    SERIAL       PRIMARY KEY,
    parent_id     INT,
    post_id       INT          NOT NULL,
    user_id       INTEGER      NOT NULL,
    creation_date TIMESTAMP    NOT NULL,
    body          TEXT         NOT NULL,
    enabled       BOOLEAN      NOT NULL,
    edited        BOOLEAN      NOT NULL,
    last_edited   TIMESTAMP,

    FOREIGN KEY (parent_id) REFERENCES COMMENTS (comment_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS_LIKES
(
    comments_likes_id   SERIAL PRIMARY KEY,
    comment_id          INTEGER     NOT NULL,
    user_id             INTEGER     NOT NULL,
    value               INTEGER     NOT NULL,
    UNIQUE (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES COMMENTS (comment_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS USERS_FOLLOWS
(
    user_id                 INTEGER     NOT NULL,
    user_follow_id          INTEGER     NOT NULL,
    FOREIGN KEY (user_follow_id) REFERENCES USERS (user_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS USER_FAV_POST
(
    user_id                 INTEGER     NOT NULL,
    post_id                 INTEGER     NOT NULL,
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);


