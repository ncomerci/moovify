CREATE TABLE IF NOT EXISTS USERS
(
    user_id         SERIAL       PRIMARY KEY,
    creation_date   TIMESTAMP    NOT NULL,
    username        VARCHAR(50)  UNIQUE NOT NULL,
    password        VARCHAR(200) NOT NULL,
    name            VARCHAR(50)  NOT NULL,
    email           VARCHAR(200) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS USER_VERIFICATION_TOKEN
(
    token_id    SERIAL       PRIMARY KEY,
    user_id     INTEGER    UNIQUE NOT NULL,
    token       TEXT         UNIQUE NOT NULL,
    expiry      TIMESTAMP    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES USERS (user_id)
);

CREATE TABLE IF NOT EXISTS ROLES
(
    role_id SERIAL      PRIMARY KEY,
    role    VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS USER_ROLE
(
    user_id    INTEGER     NOT NULL,
    role_id    INTEGER     NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES USERS (user_id),
    FOREIGN KEY (role_id) REFERENCES ROLES (role_id)
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

    FOREIGN KEY (category_id) REFERENCES POST_CATEGORY (category_id),
    FOREIGN KEY (user_id)     REFERENCES USERS (user_id)
);


CREATE TABLE IF NOT EXISTS TAGS
(
    post_id INTEGER     NOT NULL,
    tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (post_id, tag),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id)
);

CREATE TABLE IF NOT EXISTS MOVIES
(
    movie_id      SERIAL PRIMARY KEY,
    creation_date TIMESTAMP    NOT NULL,
    premier_date  DATE         NOT NULL,
    title         VARCHAR(200) NOT NULL,

    UNIQUE (title, premier_date)
);

CREATE TABLE IF NOT EXISTS POST_MOVIE
(
    post_id  INTEGER    NOT NULL,
    movie_id INTEGER    NOT NULL,
    PRIMARY KEY (post_id, movie_id),
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id),
    FOREIGN KEY (movie_id) REFERENCES MOVIES (movie_id)
);


CREATE TABLE IF NOT EXISTS COMMENTS
(
    comment_id    SERIAL       PRIMARY KEY,
    parent_id     INT,
    post_id       INT          NOT NULL,
    user_id       INTEGER      NOT NULL,
    creation_date TIMESTAMP    NOT NULL,
    body          TEXT         NOT NULL,

    FOREIGN KEY (parent_id) REFERENCES COMMENTS (comment_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES POSTS (post_id),
    FOREIGN KEY (user_id) REFERENCES USERS (user_id)
);

