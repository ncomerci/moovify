package ar.edu.itba.paw.models;

import java.time.LocalDateTime;
import java.util.Collection;

public class Comment {

    private final long id;
    private final LocalDateTime creationDate;
    private final long postId;
    private final Long parentId;
    private final Collection<Comment> children;
    private final String body;
    private final User user;

    public Comment(long id, LocalDateTime creationDate, long postId, Long parentId, Collection<Comment> children, String body, User user) {
        this.id = id;
        this.creationDate = creationDate;
        this.postId = postId;
        this.parentId = parentId;
        this.children = children;
        this.body = body;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public long getPostId(){
        return postId;
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

    public int getDescendantCount() {
        return children.stream().reduce(0, (acc, comment) -> acc + comment.getDescendantCount() + 1, Integer::sum);
    }
}
