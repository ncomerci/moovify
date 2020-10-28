package ar.edu.itba.paw.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "comments")
public class Comment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Comment.class);

    /*
    public static int getTotalComments(Collection<Comment> comments) {
        return comments.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount() + 1, Integer::sum);
    }
    */

    /*public static boolean hasUserVotedComment(Comment comment, long user_id){
        return comment.getVotedBy().containsKey(user_id);
    }

    public static boolean hasUserLikedComment(Comment comment, long user_id){
        return comment.getVotedBy().get(user_id) > 0;
    }*/

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
    private Collection<Comment> children;

    @Column(nullable = false, length = 1000)
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String body;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "comment", cascade = CascadeType.ALL)
    private Collection<CommentLike> likes;

    @Column(nullable = false)
    private boolean enabled;

    public Comment(long id, LocalDateTime creationDate, Post post, Comment parent, Collection<Comment> children, String body, User user, boolean enabled, Collection<CommentLike> likes) {
        this(creationDate, post, parent, children, body, user, enabled, likes);
        this.id = id;
    }

    public Comment(LocalDateTime creationDate, Post post, Comment parent, Collection<Comment> children, String body, User user, boolean enabled, Collection<CommentLike> likes) {
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

    public Collection<Comment> getChildren() {
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
        return likes.stream().reduce(0, (acum, commentLike) -> acum += commentLike.getValue(), Integer::sum);
    }

    /*
    public int getDescendantCount() {
        return children.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount() + 1, Integer::sum);
    }
     */


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

    public int getLikeValue(User user) {
        return getLikes().stream()
                .filter(commentLike -> commentLike.getUser().getId() == user.getId())
                .map(CommentLike::getValue)
                .findFirst().orElse(0);
    }

    public void removeLike(User user) {
        likes.removeIf(like -> like.getUser().getId() == user.getId());
    }

    public void like(User user, int value) {
        if(value == 0) {
            LOGGER.error("Tried to assign value 0 to {} like (invalid value)", this);
            return;
        }

        Optional<CommentLike> existingLike = likes.stream()
                .filter(like -> like.getUser().getId() == user.getId())
                .findFirst();

        if(existingLike.isPresent())
            existingLike.get().setValue(value);

        else
            likes.add(new CommentLike(user, this, value));
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", post=" + post.getId() +
                ", parent=" + parent.getId() +
                ", children=" + children +
//                ", body='" + body + '\'' +
                ", user=" + user.getId() +
                ", enabled=" + enabled +
                '}';
    }
}
