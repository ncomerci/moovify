package ar.edu.itba.paw.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;


@Entity
@Table(name = "posts")
public class Post {

    private static final Logger LOGGER = LoggerFactory.getLogger(Post.class);

    static public int getLikeValueByUser(Post post, User user) {
        return post.getLikes().stream().filter(postLike -> postLike.getUser().getId() == user.getId()).map(PostLike::getValue).findFirst().orElse(0);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_post_id_seq")
    @SequenceGenerator(sequenceName = "posts_post_id_seq", name = "posts_post_id_seq", allocationSize = 1)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, length = 200)
    @Basic(optional = false)
    private String title;

    @Column(nullable = false, length = 100000)
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String body;

    @Column(name = "word_count", nullable = false)
    private int wordCount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="category_id", nullable = false)
    private PostCategory category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_movie",
            joinColumns = @JoinColumn(name = "post_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "movie_id", nullable = false)
    )
    private Collection<Movie> movies;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private Collection<Comment> comments;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name="tags",
            joinColumns = @JoinColumn(name = "post_id", nullable = false)
    )
    @Column(name="tag", nullable = false)
    private Collection<String> tags;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "post", cascade = CascadeType.ALL)
    private Collection<PostLike> likes;

    @Transient
    private Long totalLikes;

    @Column(nullable = false)
    private boolean enabled;

    private static final int EN_WORDS_PER_MINUTE = 150;

    public Post(long id, LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Collection<String> tags, boolean enabled, Collection<PostLike> likes, Collection<Movie> movies, Collection<Comment> comments) {
        this(creationDate, title, body, wordCount, category, user, tags, enabled, likes, movies, comments);
        this.id = id;
    }

    public Post(LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Collection<String> tags, boolean enabled, Collection<PostLike> likes, Collection<Movie> movies, Collection<Comment> comments) {
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.user = user;
        this.category = category;
        this.tags = tags;
        this.enabled = enabled;
        this.likes = likes;
        this.movies = movies;
        this.comments = comments;
    }

    protected Post() {
        //Hibernate
    }

    @PostLoad
    public void calculateTotalLikes() {
        if(totalLikes != null)
            totalLikes = likes.stream()
                    .reduce(0L, (acum, postLike) -> acum += (long) postLike.getValue(), Long::sum);
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Collection<PostLike> getLikes(){
        return likes;
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

    public String getBody() {
        return body;
    }

    public int getWordCount() {
        return wordCount;
    }

    public User getUser() {
        return user;
    }

    public PostCategory getCategory() {
        return category;
    }

    public Collection<Movie> getMovies() {
        return movies;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public int getReadingTimeMinutes() {
        return getWordCount() / EN_WORDS_PER_MINUTE;
    }

    public Duration getTimeSinceCreation() {
        return Duration.between(creationDate, LocalDateTime.now());
    }

    public long getDaysSinceCreation() {
        return getTimeSinceCreation().toDays();
    }

    public long getHoursSinceCreation() {
        return getTimeSinceCreation().toHours();
    }

    public long getMinutesSinceCreation() {
        return getTimeSinceCreation().toMinutes();
    }

    public boolean isEnabled() { return enabled; }

    public int getLikeValue(User user) {
        return getLikes().stream()
                .filter(postLike -> postLike.getUser().getId() == user.getId())
                .map(PostLike::getValue)
                .findFirst().orElse(0);
    }

    public void delete() {
        this.enabled = false;
    }

    public void restore() {
        this.enabled = true;
    }

    public void removeLike(User user) {
        likes.removeIf(like -> like.getUser().getId() == user.getId());
    }

    public void like(User user, int value) {

        if(value == 0) {
            LOGGER.error("Tried to assign value 0 to {} like (invalid value)", this);
            return;
        }

        Optional<PostLike> existingLike = likes.stream().filter(like -> like.getUser().getId() == user.getId()).findFirst();

        if(existingLike.isPresent())
            existingLike.get().setValue(value);

        else
            likes.add(new PostLike(user, this, value));
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", title='" + title + '\'' +
//                ", body='" + body + '\'' +
                ", wordCount=" + wordCount +
                ", enabled=" + enabled +
                '}';
    }
}