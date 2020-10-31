package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {

    public static final String TABLE_NAME = "movies";
    public static final String MOVIE_TO_MOVIE_CATEGORY_TABLE_NAME = "movies";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movies_movie_id_seq")
    @SequenceGenerator(sequenceName = "movies_movie_id_seq", name = "movies_movie_id_seq", allocationSize = 1)
    @Column(name = "movie_id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, length = 200)
    @Basic(optional = false)
    private String title;

    @Column(name = "original_title", nullable = false, length = 200)
    @Basic(optional = false)
    private String originalTitle;

    @Column(name="tmdb_id", nullable = false, unique = true)
    private long tmdbId;

    @Column(name="imdb_id", nullable = false, unique = true, length = 20)
    @Basic(optional = false)
    private String imdbId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_to_movie_category",
            joinColumns = @JoinColumn(name = "tmdb_id", nullable = false, referencedColumnName = "tmdb_id"),
            inverseJoinColumns = @JoinColumn(name = "tmdb_category_id", nullable = false, referencedColumnName = "tmdb_category_id")
    )
    private Set<MovieCategory> categories;

    @Column(name = "original_language", nullable = false, length = 10)
    @Basic(optional = false)
    private String originalLanguage;

    @Column(nullable = false, length = 1000)
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String overview;

    @Column(nullable = false)
    private float popularity;

    @Column(nullable = false)
    private float runtime;

    @Column(name = "vote_average", nullable = false)
    private float voteAverage;

    @Column(name = "release_date", nullable = false)
    @Basic(optional = false)
    private LocalDate releaseDate;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "movies")
    private Set<Post> posts;

    @Transient
    private Integer postCount;

    public Movie(long id, LocalDateTime creationDate, String title, String originalTitle, long tmdbId,
                 String imdbId, String originalLanguage, String overview, float popularity, float runtime,
                 float voteAverage, LocalDate releaseDate, Set<Post> posts, Set<MovieCategory> categories) {

        this(creationDate, title, originalTitle, tmdbId, imdbId, originalLanguage, overview, popularity, runtime,
        voteAverage, releaseDate, posts, categories);
        this.id = id;
    }

    public Movie(LocalDateTime creationDate, String title, String originalTitle, long tmdbId,
                 String imdbId, String originalLanguage, String overview, float popularity, float runtime,
                 float voteAverage, LocalDate releaseDate, Set<Post> posts, Set<MovieCategory> categories) {
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
        this.posts = posts;
        this.categories = categories;
    }

    protected Movie() {
        //Hibernate
    }

    @PostLoad
    public void calculatePostCount() {
        if(postCount == null)
            postCount = posts.size();
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

    public Collection<Post> getPosts() {
        return posts;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public Collection<MovieCategory> getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", tmdbId=" + tmdbId +
                ", imdbId='" + imdbId + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", popularity=" + popularity +
                ", runtime=" + runtime +
                ", voteAverage=" + voteAverage +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
