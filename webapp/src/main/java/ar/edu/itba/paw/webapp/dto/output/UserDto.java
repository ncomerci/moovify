package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class UserDto {

    public static Collection<UserDto> mapUsersToDto(Collection<User> users, UriInfo uriInfo, SecurityContext securityContext) {
        return users.stream().map(u -> new UserDto(u, uriInfo, securityContext)).collect(Collectors.toList());
    }

    public static UriBuilder getUserUriBuilder(User user, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(user.getId()));
    }

    private long id;
    private LocalDateTime creationDate;
    private String username;
    private String name;
    private String email;
    private String description;
    private String language;
    private Long totalLikes;
    private Integer followerCount;
    private boolean enabled;
    private Collection<Role> roles;

    // Relations
    private String avatar;
    private String posts;
    private String comments;
    private String following;
    private String bookmarkedPosts;

    private String url;

    public UserDto() {
        // Do not use
    }

    public UserDto(User user, UriInfo uriInfo, SecurityContext securityContext) {

        final UriBuilder userUriBuilder = getUserUriBuilder(user, uriInfo);

        id = user.getId();
        enabled = user.isEnabled();
        url = userUriBuilder.build().toString();

        if(!enabled && !securityContext.isUserInRole(Role.ADMIN.name()))
            return;

        creationDate = user.getCreationDate();
        username = user.getUsername();
        name = user.getName();
        email = user.getEmail();
        description = user.getDescription();
        language = user.getLanguage();
        totalLikes = user.getTotalLikes();
        followerCount = user.getFollowerCount();
        roles = user.getRoles();

        avatar = userUriBuilder.clone().path("/avatar").build().toString();
        posts = userUriBuilder.clone().path("/posts").build().toString();
        comments = userUriBuilder.clone().path("/comments").build().toString();
        following = userUriBuilder.clone().path("/following").build().toString();
        bookmarkedPosts = userUriBuilder.clone().path("/bookmarked").build().toString();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getBookmarkedPosts() {
        return bookmarkedPosts;
    }

    public void setBookmarkedPosts(String bookmarkedPosts) {
        this.bookmarkedPosts = bookmarkedPosts;
    }
}
