package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentDto {

    public static Collection<CommentDto> mapCommentsToDto(Collection<Comment> comments, UriInfo uriInfo) {
        return comments.stream().map(c -> new CommentDto(c, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getCommentUriBuilder(Comment comment, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("comment").path(String.valueOf(comment.getId()));
    }

    private Long id;
    private LocalDateTime creationDateTime;
    private Post post;
    private Comment parent;
    private String body;
    private User user;
    private boolean edited;
    private LocalDateTime lastEditTime;
    private Long totalVotes;
    private boolean enabled;

    private String children;
    private String votes;

    private String url;

    public CommentDto() {
        // For Jersey Reflection - Do not use
    }

    public CommentDto(Comment comment, UriInfo uriInfo) {

        this.id = comment.getId();
        this.creationDateTime = comment.getCreationDate();
        this.post = comment.getPost();
        this.parent = comment.getParent();
        this.body = comment.getBody();
        this.user = comment.getUser();
        this.edited = comment.isEdited();
        this.lastEditTime = comment.getLastEditDate();
        this.totalVotes = comment.getTotalLikes();
        this.enabled = comment.isEnabled();

        final UriBuilder commentUriBuilder = getCommentUriBuilder(comment, uriInfo);

        children = commentUriBuilder.clone().path("/children").build().toString();
        votes = commentUriBuilder.clone().path("/votes").build().toString();

        url = commentUriBuilder.build().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public LocalDateTime getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(LocalDateTime lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
