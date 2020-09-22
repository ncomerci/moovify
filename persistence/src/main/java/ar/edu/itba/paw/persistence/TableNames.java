package ar.edu.itba.paw.persistence;

public enum TableNames {

    USERS("users"),
    ROLES("roles"),
    USER_ROLE("user_role"),
    POSTS("posts"),
    MOVIES("movies"),
    POST_MOVIE("post_movie"),
    TAGS("tags"),
    COMMENTS("comments"),
    POST_CATEGORY("post_category"),
    USER_VERIFICATION_TOKEN("user_verification_token"),
    PASSWORD_RESET_TOKEN("password_reset_token");

    private final String tableName;

    TableNames(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
