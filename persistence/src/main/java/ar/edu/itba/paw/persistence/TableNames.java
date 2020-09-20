package ar.edu.itba.paw.persistence;

public enum TableNames {
    //TODO: preguntar si es una buena forma de generalizar el nombre de la tabla
    USERS("users"),
    ROLES("roles"),
    USER_ROLE("user_role"),
    POSTS("posts"),
    MOVIES("movies"),
    POST_MOVIE("post_movie"),
    TAGS("tags"),
    COMMENTS("comments"),
    POST_CATEGORY("post_category");

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
