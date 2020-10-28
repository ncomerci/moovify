package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;


@Entity
@Table(name = "posts")
public class Post {

    static public int hasUserLiked(Post post, User user){
        return post.getLikes().stream().filter(postLikes -> postLikes.getUser().getId() == user.getId()).map(PostLikes::getValue).findFirst().orElse(0);
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

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name="tags",
            joinColumns=@JoinColumn(name="post_id", nullable = false)
    )
    @Column(name="tag", nullable = false)
    private Collection<String> tags;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "post", cascade = CascadeType.ALL)
    private Collection<PostLikes> likes;

    @Column(nullable = false)
    private boolean enabled;

    private static final int EN_WORDS_PER_MINUTE = 150;

    public Post(long id, LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Collection<String> tags, boolean enabled, Collection<PostLikes> likes) {
        this.id = id;
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.user = user;
        this.category = category;
        this.tags = tags;
        this.enabled = enabled;
        this.likes = likes;
    }

    public Post(LocalDateTime creationDate, String title, String body, int wordCount, PostCategory category, User user, Collection<String> tags, boolean enabled, Collection<PostLikes> likes) {
        this.creationDate = creationDate;
        this.title = title;
        this.body = body;
        this.wordCount = wordCount;
        this.user = user;
        this.category = category;
        this.tags = tags;
        this.enabled = enabled;
        this.likes = likes;
    }

    protected Post() {
        //Hibernate
    }

    public long getTotalLikes() {
        return likes.stream().reduce(0, (acum, postLikes) -> acum+=postLikes.getValue(), Integer::sum);
    }

    public Collection<PostLikes> getLikes(){
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

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", title='" + title + '\'' +
//                ", body='" + body + '\'' +
                ", wordCount=" + wordCount +
                ", user=" + user.getId() +
                ", category=" + category +
                ", tags=" + tags +
                ", likes=" + likes +
                ", enabled=" + enabled +
                '}';
    }
}