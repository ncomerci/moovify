package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.Role;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class PostDto {

    public static Collection<PostDto> mapPostsToDto(Collection<Post> posts, UriInfo uriInfo, SecurityContext securityContext) {
        return posts.stream().map(p -> new PostDto(p, uriInfo, securityContext)).collect(Collectors.toList());
    }

    public static UriBuilder getPostUriBuilder(Post post, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(post.getId()));
    }

    private long id;
    private LocalDateTime creationDate;
    private String title;
    private String body;
    private Integer wordCount;
    private Boolean edited;
    private LocalDateTime lastEditDate;
    private UserDto user;
    private PostCategoryDto postCategory;
    private Collection<String> tags;
    private Collection<MovieDto> movies;
    private boolean enabled;
    private Long totalLikes;

    // Relations
    private String comments;
    private String votes;

    private String url;

    public PostDto(){
        // For Jersey - Do not use
    }

    public PostDto(Post post, UriInfo uriInfo, SecurityContext securityContext) {

        final UriBuilder postUriBuilder = getPostUriBuilder(post, uriInfo);

        id = post.getId();
        enabled = post.isEnabled();
        url = postUriBuilder.build().toString();

        if(!enabled && !securityContext.isUserInRole(Role.ADMIN.name()))
            return;

        creationDate = post.getCreationDate();
        title = post.getTitle();
        body = post.getBody();
        wordCount = post.getWordCount();
        edited = post.isEdited();
        lastEditDate = post.getLastEditDate();
        user = new UserDto(post.getUser(), uriInfo, securityContext);
        postCategory = new PostCategoryDto(post.getCategory());
        tags = post.getTags();
        movies = MovieDto.mapMoviesToDto(post.getMovies(), uriInfo);
        totalLikes = post.getTotalVotes();

        comments = postUriBuilder.clone().path("comments").build().toString();
        votes = postUriBuilder.clone().path("votes").build().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Boolean isEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public LocalDateTime getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(LocalDateTime lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public PostCategoryDto getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(PostCategoryDto postCategory) {
        this.postCategory = postCategory;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Collection<MovieDto> getMovies() {
        return movies;
    }

    public void setMovies(Collection<MovieDto> movies) {
        this.movies = movies;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
