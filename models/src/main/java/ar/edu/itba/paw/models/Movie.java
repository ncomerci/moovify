package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movies_movie_id_seq")
    @SequenceGenerator(sequenceName = "movies_movie_id_seq", name = "movies_movie_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, length = 200)
    @Basic(optional = false)
    private String title;

    @Column(name = "original_title", nullable = false, length = 200)
    @Basic(optional = false)
    private String originalTitle;

    @Column(nullable = false, unique = true)
    private long tmdbId;

    @Column(nullable = false, unique = true, length = 20)
    @Basic(optional = false)
    private String imdbId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_to_movie_category",
            joinColumns = @JoinColumn(name = "tmdb_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tmdb_category_id", nullable = false)
    )
    private Collection<MovieCategory> movieCategories;

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
    private Collection<Post> posts;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "post", cascade = CascadeType.ALL)
    private Collection<MovieCategory> categories;

    public Movie(long id, LocalDateTime creationDate, String title, String originalTitle, long tmdbId,
                 String imdbId, String originalLanguage, String overview, float popularity, float runtime,
                 float voteAverage, LocalDate releaseDate, Collection<Post> posts, Collection<MovieCategory> categories) {

        this(creationDate, title, originalTitle, tmdbId, imdbId, originalLanguage, overview, popularity, runtime,
        voteAverage, releaseDate, posts, categories);
        this.id = id;
    }

    public Movie(LocalDateTime creationDate, String title, String originalTitle, long tmdbId,
                 String imdbId, String originalLanguage, String overview, float popularity, float runtime,
                 float voteAverage, LocalDate releaseDate, Collection<Post> posts, Collection<MovieCategory> categories) {
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
    /* public long getPostCount() {
        return posts.size();
    }*/

    public Collection<MovieCategory> getCategories() {
        return categories;
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
                ", overview='" + overview + '\'' +
                ", popularity=" + popularity +
                ", runtime=" + runtime +
                ", voteAverage=" + voteAverage +
                ", releaseDate=" + releaseDate +
                ", posts=" + posts +
                ", categories=" + categories +
                '}';
    }
}
