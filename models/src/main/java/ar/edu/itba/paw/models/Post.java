package ar.edu.itba.paw.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Entity
@Table(name = "posts")
public class Post {

    private static final Logger LOGGER = LoggerFactory.getLogger(Post.class);

    public static final String TABLE_NAME = "posts";
    public static final String POST_MOVIE_TABLE_NAME = "post_movie";
    public static final String TAGS_TABLE_NAME = "tags";

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

    @Column(nullable = false)
    private boolean edited;

    @Column(name = "last_edited", nullable = true)
    @Basic(optional = true)
    private LocalDateTime lastEditDate;

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
    private Set<Movie> movies;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private Set<Comment> comments;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name="tags",
            joinColumns = @JoinColumn(name = "post_id", nullable = false)
    )
    @Column(name="tag", nullable = false)
    private Set<String> tags;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostVote> votes;

    @Transient
    private Long totalVotes;

    @Column(nullable = false)
    private boolean enabled;

    private static final int EN_WORDS_PER_MINUTE = 150;

    public Post(long id, LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, boolean edited, LocalDateTime lastEditDate, boolean enabled, Set<PostVote> votes, Set<Movie> movies, Set<Comment> comments) {
        this(creationDate, title, body, wordCount, category, user, tags, edited, lastEditDate, enabled, votes, movies, comments);
        this.id = id;
    }

    public Post(LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Set<String> tags, boolean edited, LocalDateTime lastEditDate, boolean enabled, Set<PostVote> votes, Set<Movie> movies, Set<Comment> comments) {
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.user = user;
        this.category = category;
        this.tags = tags;
        this.edited = edited;
        this.lastEditDate = lastEditDate;
        this.enabled = enabled;
        this.votes = votes;
        this.movies = movies;
        this.comments = comments;
    }

    protected Post() {
        //Hibernate
    }

    public void calculateTotalLikes() {
        if(totalVotes == null)
            totalVotes = votes.stream()
                    .reduce(0L, (acum, postLike) -> acum += (long) postLike.getValue(), Long::sum);
    }

    public long getTotalVotes() {
        if(totalVotes == null)
            calculateTotalLikes();

        return totalVotes;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Collection<PostVote> getVotes(){
        return votes;
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

    public void setBody(String body) {
        edited = true;
        lastEditDate = LocalDateTime.now();
        this.body = body;
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

    public boolean isEdited() {
        return edited;
    }

    public LocalDateTime getLastEditDate() {
        return lastEditDate;
    }

    public int getReadingTimeMinutes() {
        return getWordCount() / EN_WORDS_PER_MINUTE;
    }

    public Duration getTimeSinceCreation() {
        return Duration.between(getCreationDate(), LocalDateTime.now());
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

    public int getVoteValue(User user) {
        return getVotes().stream()
                .filter(postLike -> postLike.getUser().getId() == user.getId())
                .map(PostVote::getValue)
                .findFirst().orElse(0);
    }

    public void delete() {
        this.enabled = false;
    }

    public void restore() {
        this.enabled = true;
    }

    public void removeVote(User user) {

        final Optional<PostVote> optLike =
                getVotes().stream().filter(like -> like.getUser().getId() == user.getId()).findFirst();

        if(optLike.isPresent()) {
            final PostVote like = optLike.get();

            like.getUser().removePostLike(like);
            getVotes().remove(like);
        }
    }

    public void vote(User user, int value) {

        if(value == 0) {
            LOGGER.error("Tried to assign value 0 to {} like (invalid value)", this);
            return;
        }

        Optional<PostVote> existingVote =
                getVotes().stream().filter(like -> like.getUser().getId() == user.getId()).findFirst();

        if(existingVote.isPresent())
            existingVote.get().setValue(value);

        else {
            final PostVote vote = new PostVote(user, this, value);

            user.addPostLike(vote);
            getVotes().add(vote);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", title='" + title + '\'' +
                ", wordCount=" + wordCount +
                ", enabled=" + enabled +
                '}';
    }
}