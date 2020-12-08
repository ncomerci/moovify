package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Comment;

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
        return uriInfo.getBaseUriBuilder().path("comments").path(String.valueOf(comment.getId()));
    }

    private long id;
    private LocalDateTime creationDateTime;
    private PostDto post;
    private String body;
    private UserDto user;
    private boolean edited;
    private LocalDateTime lastEditTime;
    private long totalVotes;
    private boolean enabled;

    private String parent;
    private String children;
    private String votes;

    private String url;

    public CommentDto() {
        // For Jersey Reflection - Do not use
    }

    public CommentDto(Comment comment, UriInfo uriInfo) {

        id = comment.getId();
        creationDateTime = comment.getCreationDate();
        post = new PostDto(comment.getPost(), uriInfo);
        body = comment.getBody();
        user = new UserDto(comment.getUser(), uriInfo);
        edited = comment.isEdited();
        lastEditTime = comment.getLastEditDate();
        totalVotes = comment.getTotalVotes();
        enabled = comment.isEnabled();

        final UriBuilder commentUriBuilder = getCommentUriBuilder(comment, uriInfo);

        parent = getCommentUriBuilder(comment.getParent(), uriInfo).build().toString();
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

    public PostDto getPost() {
        return post;
    }

    public void setPost(PostDto post) {
        this.post = post;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
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
