package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Role;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentDto {

    public static Collection<CommentDto> mapCommentsToDto(Collection<Comment> comments, UriInfo uriInfo, SecurityContext securityContext) {
        return comments.stream().map(c -> new CommentDto(c, uriInfo, securityContext)).collect(Collectors.toList());
    }

    public static UriBuilder getCommentUriBuilder(Comment comment, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("comments").path(String.valueOf(comment.getId()));
    }

    private long id;
    private LocalDateTime creationDateTime;
    private LightweightPostDto post;
    private String body;
    private UserDto user;
    private Boolean edited;
    private LocalDateTime lastEditTime;
    private Long totalVotes;
    private boolean enabled;
    private Integer userVote;
    private Boolean isOwner;

    private String parent;
    private String children;
    private String votes;

    private String url;

    public CommentDto() {
        // For Jersey Reflection - Do not use
    }

    public CommentDto(Comment comment, UriInfo uriInfo, SecurityContext securityContext) {

        final UriBuilder commentUriBuilder = getCommentUriBuilder(comment, uriInfo);

        id = comment.getId();
        enabled = comment.isEnabled();

        children = commentUriBuilder.clone().path("/children").build().toString();
        url = commentUriBuilder.build().toString();

        if(!enabled && !securityContext.isUserInRole(Role.ADMIN.name()))
            return;

        creationDateTime = comment.getCreationDate();
        post = new LightweightPostDto(comment.getPost(), uriInfo, securityContext);
        body = comment.getBody();
        user = new UserDto(comment.getUser(), uriInfo, securityContext);
        edited = comment.isEdited();
        lastEditTime = comment.getLastEditDate();
        totalVotes = comment.getTotalVotes();

        if(securityContext.getUserPrincipal() != null) {
            final String loggedUserUsername = securityContext.getUserPrincipal().getName();

            userVote = comment.getVoteValue(loggedUserUsername);
            isOwner = comment.getUser().getUsername().equals(loggedUserUsername);
        }
        
        if(comment.getParent() != null)
            parent = getCommentUriBuilder(comment.getParent(), uriInfo).build().toString();

        else
            parent = null;

        children = commentUriBuilder.clone().path("/children").build().toString();
        votes = commentUriBuilder.clone().path("/votes").build().toString();

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

    public LightweightPostDto getPost() {
        return post;
    }

    public void setPost(LightweightPostDto post) {
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

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
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

    public Integer getUserVote() {
        return userVote;
    }

    public void setUserVote(Integer userVote) {
        this.userVote = userVote;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }
}
