package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
