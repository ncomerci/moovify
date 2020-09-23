package ar.edu.itba.paw.models;

public class MovieCategory {

    private final long id;
    private final long tmdbCategoryId;
    private final String name;

    public MovieCategory(long id, long tmdbCategoryId, String name) {
        this.id = id;
        this.tmdbCategoryId = tmdbCategoryId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public long getTmdb_id() {
        return tmdbCategoryId;
    }

    public String getName() {
        return name;
    }
}