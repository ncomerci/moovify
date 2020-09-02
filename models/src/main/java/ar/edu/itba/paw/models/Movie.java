package ar.edu.itba.paw.models;

import java.time.LocalDate;

public class Movie {

    private final long id;
    private final String name;
    private final LocalDate premierDate;

    public Movie(long id, String name, LocalDate premierDate) {
        this.id = id;
        this.name = name;
        this.premierDate = premierDate;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getPremierDate() {
        return premierDate;
    }
}
