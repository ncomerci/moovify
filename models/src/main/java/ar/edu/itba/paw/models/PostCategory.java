package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class PostCategory {

    private final long id;
    private final LocalDateTime creationDate;
    private final String name;

    public PostCategory(long id, LocalDateTime creationDate, String name) {
        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getName() {
        return name;
    }
}
