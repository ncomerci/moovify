package ar.edu.itba.paw.persistence;

enum TableNames {
    //TODO: preguntar si es una buena forma de generalizar el nombre de la tabla
    POST("posts"),
    MOVIES("movies"),
    POST_MOVIES("postMovie");

    private final String tableName;

    TableNames(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
