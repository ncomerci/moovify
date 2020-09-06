package ar.edu.itba.paw.models;

import java.time.LocalDateTime;
import java.util.Collection;

public class Comment {

    private final long id;
    private final LocalDateTime creationDate;
    private final long postId;
    private final long parentId;
    private final Collection<Comment> children;
    private final String body;
    private final String userEmail; // Temporary

    public Comment(long id, LocalDateTime creationDate, long postId, long parentId, Collection<Comment> children, String body, String userEmail) {
        this.id = id;
        this.creationDate = creationDate;
        this.postId = postId;
        this.parentId = parentId;
        this.children = children;
        this.body = body;
        this.userEmail = userEmail;
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

    public long getParentId() {
        return parentId;
    }

    public Collection<Comment> getChildren() {
        return children;
    }

    public String getBody() {
        return body;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
