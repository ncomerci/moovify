package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.*;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class PostDto {

    public static Collection<PostDto> mapPostsToDto(Collection<Post> posts, UriInfo uriInfo) {
        return posts.stream().map(p -> new PostDto(p, uriInfo)).collect(Collectors.toList());
    }

    public static UriBuilder getPostUriBuilder(Post post, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(post.getId()));
    }

    private long id;
    private LocalDateTime creationDate;
    private String title;
    private String body;
    private int wordCount;
    private boolean edited;
    private LocalDateTime lastEditDate;
    private User user;
    private String postCategory;
    private Collection<String> tags;
    private Collection<Movie> movies;
    private boolean enabled;
    private Long totalLikes;

    // Relations
    private String comments;
    private String votes;

    private String url;

    public PostDto(){
        // Do not use
    }

    public PostDto(Post post, UriInfo uriInfo) {
        this.id = post.getId();
        this.creationDate = post.getCreationDate();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.wordCount = post.getWordCount();
        this.edited = post.isEdited();
        this.lastEditDate = post.getLastEditDate();
        this.user = post.getUser();
        this.postCategory = post.getCategory().getName();
        this.tags = post.getTags();
        this.movies = post.getMovies();
        this.enabled = post.isEnabled();
        this.totalLikes = post.getTotalLikes();

        final UriBuilder postUriBuilder = getPostUriBuilder(post, uriInfo);

        comments = postUriBuilder.clone().path("/comments").build().toString();
        votes = postUriBuilder.clone().path("/votes").build().toString();

        url = postUriBuilder.build().toString();
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

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public LocalDateTime getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(LocalDateTime lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public Collection<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Collection<Movie> movies) {
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
