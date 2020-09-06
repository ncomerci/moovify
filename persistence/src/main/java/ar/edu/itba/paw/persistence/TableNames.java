package ar.edu.itba.paw.persistence;

enum TableNames {
    //TODO: preguntar si es una buena forma de generalizar el nombre de la tabla
    POSTS("posts"),
    MOVIES("movies"),
    POST_MOVIE("post_movie"),
    TAGS("tags"),
    COMMENTS("comments");

    private final String tableName;

    TableNames(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
