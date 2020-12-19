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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_comment_id_seq")
    @SequenceGenerator(sequenceName = "comments_comment_id_seq", name = "comments_comment_id_seq", allocationSize = 1)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    @Column(nullable = false)
    private boolean edited;

    @Column(name = "last_edited", nullable = true)
    @Basic(optional = true)
    private LocalDateTime lastEditDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "comment", cascade = CascadeType.ALL)
    private Set<CommentVote> votes;

    @Transient
    private Long totalVotes;

    @Column(nullable = false)
    private boolean enabled;

    public Comment(long id, LocalDateTime creationDate, Post post, Comment parent, Set<Comment> children, String body, boolean edited, LocalDateTime lastEditDate, User user, boolean enabled, Set<CommentVote> votes) {
        this(creationDate, post, parent, children, body, edited, lastEditDate, user, enabled, votes);
        this.id = id;
    }

    public Comment(LocalDateTime creationDate, Post post, Comment parent, Set<Comment> children, String body, boolean edited, LocalDateTime lastEditDate, User user, boolean enabled, Set<CommentVote> votes) {
        this.creationDate = creationDate;
        this.post = post;
        this.parent = parent;
        this.children = children;
        this.body = body;
        this.edited = edited;
        this.lastEditDate = lastEditDate;
        this.user = user;
        this.enabled = enabled;
        this.votes = votes;
    }

    protected Comment() {
        //Hibernate
    }

    public void calculateTotalLikes() {
        if(totalVotes == null)
            totalVotes = votes.stream()
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

    public void setBody(String body) {
        edited = true;
        lastEditDate = LocalDateTime.now();
        this.body = body;
    }

    public boolean isEdited() {
        return edited;
    }

    public LocalDateTime getLastEditDate() {
        return lastEditDate;
    }

    public User getUser() {
        return user;
    }

    public Collection<CommentVote> getVotes() {
        return votes;
    }

    public long getTotalVotes() {
        if(totalVotes == null)
            calculateTotalLikes();

        return totalVotes;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getVoteValue(User user) {
        return getVotes().stream()
                .filter(commentLike -> commentLike.getUser().getId() == user.getId())
                .map(CommentVote::getValue)
                .findFirst().orElse(0);
    }

    public int getVoteValue(String username) {
        return getVotes().stream()
                .filter(commentLike -> commentLike.getUser().getUsername().equals(username))
                .map(CommentVote::getValue)
                .findFirst().orElse(0);
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

        final Optional<CommentVote> optLike =
                getVotes().stream().filter(like -> like.getUser().getId() == user.getId()).findFirst();

        if(optLike.isPresent()) {
            final CommentVote like = optLike.get();

            like.getUser().removeCommentLike(like);
            getVotes().remove(like);
        }
    }

    public void like(User user, int value) {
        if(value == 0) {
            LOGGER.error("Tried to assign value 0 to {} like (invalid value)", this);
            return;
        }

        final Optional<CommentVote> existingLike = votes.stream()
                .filter(like -> like.getUser().getId() == user.getId())
                .findFirst();

        if(existingLike.isPresent())
            existingLike.get().setValue(value);

        else {
            final CommentVote like = new CommentVote(user, this, value);

            user.addCommentLike(like);
            getVotes().add(like);
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
                ", parent=" + (parent != null ? parent.getId() : 0) +
                '}';
    }
}
