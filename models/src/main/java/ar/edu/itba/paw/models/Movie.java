package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public class Movie {

    private final long id;
    private final LocalDateTime creationDate;
    private final String title;
    private final String originalTitle;
    private final long tmdbId;
    private final String imdbId;
    private final String originalLanguage;
    private final String overview;
    private final float popularity;
    private final float runtime;
    private final float voteAverage;
    private final LocalDate releaseDate;
    private final long postCount;
    private final Collection<MovieCategory> categories;

    public Movie(long id, LocalDateTime creationDate, String title, String originalTitle, long tmdbId,
                 String imdbId, String originalLanguage, String overview, float popularity, float runtime,
                 float voteAverage, LocalDate releaseDate, long postCount, Collection<MovieCategory> categories) {
        this.id = id;
        this.creationDate = creationDate;
        this.title = title;
        this.originalTitle = originalTitle;
        this.tmdbId = tmdbId;
        this.imdbId = imdbId;
        this.originalLanguage = originalLanguage;
        this.overview = overview;
        this.popularity = popularity;
        this.runtime = runtime;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.postCount = postCount;
        this.categories = categories;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public long getTmdbId() {
        return tmdbId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOverview() {
        return overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public float getRuntime() {
        return runtime;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public long getPostCount() {
        return postCount;
    }

    public Collection<MovieCategory> getCategories() {
        return categories;
    }
}
