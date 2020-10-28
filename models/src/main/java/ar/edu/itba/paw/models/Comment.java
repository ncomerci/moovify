package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Entity
@Table(name = "comments")
public class Comment {

    public static int getTotalComments(Collection<Comment> comments) {
        return comments.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount() + 1, Integer::sum);
    }

    /*public static boolean hasUserVotedComment(Comment comment, long user_id){
        return comment.getVotedBy().containsKey(user_id);
    }

    public static boolean hasUserLikedComment(Comment comment, long user_id){
        return comment.getVotedBy().get(user_id) > 0;
    }*/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_comment_id_seq")
    @SequenceGenerator(sequenceName = "comments_comment_id_seq", name = "comments_comment_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true)
    private Comment parent;

   @OneToMany(fetch = FetchType.LAZY, orphanRemoval = false, mappedBy = "parentId")
   private Collection<Comment> children;

    @Column(nullable = false, length = 1000)
    @Basic(optional = false, fetch = FetchType.LAZY)
    private String body;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "comment", cascade = CascadeType.ALL)
    private Collection<CommentsLikes> likes;

    @Column(nullable = false)
    private boolean enabled;

    public Comment(long id, LocalDateTime creationDate, Post post, Comment parent, Collection<Comment> children, String body, User user, boolean enabled, Collection<CommentsLikes> likes) {
        this.id = id;
        this.creationDate = creationDate;
        this.post = post;
        this.parent = parent;
        this.children = children;
        this.body = body;
        this.user = user;
        this.enabled = enabled;
        this.likes = likes;
    }

    public Comment(LocalDateTime creationDate, Post post, Comment parent, Collection<Comment> children, String body, User user, boolean enabled, Collection<CommentsLikes> likes) {
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

    // May return null when comment is root
    public Long getParentId() {
        return parentId;
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

    public long getLikes() {
        return likes.stream().reduce(0, (acum, commentsLikes) -> acum+=commentsLikes.getValue(), Integer::sum);
    }

    public Map<Long, Integer> getVotedBy() {
        return votedBy;
    }

    public int getDescendantCount() {
        return children.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount() + 1, Integer::sum);
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

    public int getUserVote(User user) {
        if(!getVotedBy().containsKey(user.getId()))
            return 0;

        return getVotedBy().get(user.getId());
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", post=" + post.getId() +
                ", parentId=" + parentId +
                ", children=" + children +
//                ", body='" + body + '\'' +
                ", user=" + user.getId() +
                ", likes=" + likes +
                ", votedBy=" + votedBy +
                ", enabled=" + enabled +
                '}';
    }
}
