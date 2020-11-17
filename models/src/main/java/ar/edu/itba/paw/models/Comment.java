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
@Table(name = "comments")
public class Comment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Comment.class);

    public static final String TABLE_NAME = "comments";

    static public int getLikeValue(Comment comment, User user) {
        return comment.getLikes().stream()
                .filter(commentLike -> commentLike.getUser().getId() == user.getId())
                .map(CommentLike::getValue)
                .findFirst().orElse(0);
    }

    public static int getDescendantCount(Comment comment, long maxDepth) {
        return comment.getDescendantCount(maxDepth);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_comment_id_seq")
    @SequenceGenerator(sequenceName = "comments_comment_id_seq", name = "comments_comment_id_seq", allocationSize = 1)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true, referencedColumnName = "comment_id")
    private Comment parent;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = false, mappedBy = "parent")
    private Set<Comment> children;

    @Column(nullable = false, length = 1000)
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String body;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "comment", cascade = CascadeType.ALL)
    private Set<CommentLike> likes;

    @Transient
    private Long totalLikes;

    @Column(nullable = false)
    private boolean enabled;

    public Comment(long id, LocalDateTime creationDate, Post post, Comment parent, Set<Comment> children, String body, User user, boolean enabled, Set<CommentLike> likes) {
        this(creationDate, post, parent, children, body, user, enabled, likes);
        this.id = id;
    }

    public Comment(LocalDateTime creationDate, Post post, Comment parent, Set<Comment> children, String body, User user, boolean enabled, Set<CommentLike> likes) {
        this.creationDate = creationDate;
        this.post = post;
        this.parent = parent;
        this.children = children;
        this.body = body;
        this.user = user;
        this.enabled = enabled;
        this.likes = likes;
    }


    protected Comment() {
        //Hibernate
    }

    @PostLoad
    public void calculateTotalLikes() {
        if(totalLikes == null)
            totalLikes = likes.stream()
                .reduce(0L, (acum, commentLike) -> acum += (long) commentLike.getValue(), Long::sum);
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Post getPost(){
        return post;
    }

    // May return null when post is root
    public Comment getParent() {
        return parent;
    }

    public Set<Comment> getChildren() {
        return children;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public Collection<CommentLike> getLikes() {
        return likes;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getDescendantCount(long maxDepth) {
        if(maxDepth <= 1)
            return 0;

        return children.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount(maxDepth - 1) + 1, Integer::sum);
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void removeLike(User user) {

        final Optional<CommentLike> optLike =
                getLikes().stream().filter(like -> like.getUser().getId() == user.getId()).findFirst();

        if(optLike.isPresent()) {
            final CommentLike like = optLike.get();

            like.getUser().removeCommentLike(like);
            getLikes().remove(like);
        }
    }

    public void like(User user, int value) {
        if(value == 0) {
            LOGGER.error("Tried to assign value 0 to {} like (invalid value)", this);
            return;
        }

        final Optional<CommentLike> existingLike = likes.stream()
                .filter(like -> like.getUser().getId() == user.getId())
                .findFirst();

        if(existingLike.isPresent())
            existingLike.get().setValue(value);

        else {
            final CommentLike like = new CommentLike(user, this, value);

            user.addCommentLike(like);
            getLikes().add(like);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", children=" + children +
                ", enabled=" + enabled +
                '}';
    }
}
