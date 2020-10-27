package ar.edu.itba.paw.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public class Comment {

    public static int getTotalComments(Collection<Comment> comments) {
        return comments.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount() + 1, Integer::sum);
    }

    public static boolean hasUserVotedComment(Comment comment, long user_id){
        return comment.getVotedBy().containsKey(user_id);
    }

    public static boolean hasUserLikedComment(Comment comment, long user_id){
        return comment.getVotedBy().get(user_id) > 0;
    }

    private final long id;
    private final LocalDateTime creationDate;
    private final Post post;
    private final Long parentId;
    private final Collection<Comment> children;
    private final String body;
    private final User user;
    private final long likes;
    private final Map<Long, Integer> votedBy;
    private final boolean enabled;


    public Comment(long id, LocalDateTime creationDate, Post post, Long parentId, Collection<Comment> children, String body, User user, boolean enabled, long likes, Map<Long, Integer> votedBy) {
        this.id = id;
        this.creationDate = creationDate;
        this.post = post;
        this.parentId = parentId;
        this.children = children;
        this.body = body;
        this.user = user;
        this.enabled = enabled;
        this.likes = likes;
        this.votedBy = votedBy;
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
        return likes;
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
