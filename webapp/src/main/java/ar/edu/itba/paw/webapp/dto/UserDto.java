package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class UserDto {

    public static Collection<UserDto> mapUsersToDto(Collection<User> users, UriInfo uriInfo) {
        return users.stream().map(u -> new UserDto(u, uriInfo)).collect(Collectors.toList());
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
    private long totalLikes;
    private boolean enabled;

    // Not sent
    private String password;

    private String url;

    public UserDto() {
        // Do not use
    }

    public UserDto(User user, UriInfo uriInfo) {
        id = user.getId();
        creationDate = user.getCreationDate();
        username = user.getUsername();
        name = user.getName();
        email = user.getEmail();
        description = user.getDescription();
        language = user.getLanguage();
        totalLikes = user.getTotalLikes();
        enabled = user.isEnabled();

        url = getUserUriBuilder(user, uriInfo).build().toString();
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

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
