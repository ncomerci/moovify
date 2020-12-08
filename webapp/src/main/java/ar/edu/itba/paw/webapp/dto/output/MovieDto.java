package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Movie;
import ar.edu.itba.paw.models.MovieCategory;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class MovieDto {

    public static Collection<MovieDto> mapMoviesToDto(Collection<Movie> movies, UriInfo uriInfo) {
        return movies.stream().map(m -> new MovieDto(m, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getMovieUriBuilder(Movie movie, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("movies").path(String.valueOf(movie.getId()));
    }

    private long id;
    private LocalDateTime creationDate;
    private String title;
    private String originalTitle;
    private long tmdbId;
    private String imdbId;
    private Collection<String> categories;
    private String originalLanguage;
    private String overview;
    private float popularity;
    private float runtime;
    private float voteAverage;
    private LocalDate releaseDate;

    private String poster;
    private String posts;
    private String url;

    public MovieDto() {
        //For jersey - Do not use
    }

    public MovieDto(Movie movie, UriInfo uriInfo) {
        this.id = movie.getId();
        this.creationDate = movie.getCreationDate();
        this.title = movie.getTitle();
        this.originalTitle = movie.getOriginalTitle();
        this.tmdbId = movie.getTmdbId();
        this.imdbId = movie.getImdbId();
        this.categories = movie.getCategories().stream().map(MovieCategory::getName).collect(Collectors.toList());
        this.originalLanguage = movie.getOriginalLanguage();
        this.overview = movie.getOverview();
        this.popularity = movie.getPopularity();
        this.runtime = movie.getRuntime();
        this.voteAverage = movie.getVoteAverage();
        this.releaseDate = movie.getReleaseDate();

        final UriBuilder movieUriBuilder = getMovieUriBuilder(movie, uriInfo);

        poster = movieUriBuilder.clone().path("/poster").build().toString();
        posts = movieUriBuilder.clone().path("/posts").build().toString();
        url = movieUriBuilder.build().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public Collection<String> getCategories() {
        return categories;
    }

    public void setCategories(Collection<String> categories) {
        this.categories = categories;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public float getRuntime() {
        return runtime;
    }

    public void setRuntime(float runtime) {
        this.runtime = runtime;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
