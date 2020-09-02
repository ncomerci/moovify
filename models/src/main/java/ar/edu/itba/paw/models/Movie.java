package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.util.Objects;

public class Movie {

    private final long id;
    private final String title;
    private final LocalDate premierDate;

    public Movie(long id, String title, LocalDate premierDate) {
        this.id = id;
        this.title = title;
        this.premierDate = premierDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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
