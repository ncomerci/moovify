package ar.edu.itba.paw.models;

import java.time.LocalDateTime;
import java.util.Collection;

public class User {

    private final long id;
    private final LocalDateTime creationDate;
    private final String username;
    private final String password;
    private final String name;
    private final String email;
    private final String description;
    private final Collection<Role> roles;
    private final Collection<Long> likedComments;
    private final boolean enabled;

    public static boolean hasUserLikedComment(User user, long comment_id){
        return user.getLikedComments().contains(comment_id);
    }

    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, String description, Collection<Role> roles, boolean enabled, Collection<Long> likedComments) {
        this.id = id;
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.description = description;
        this.roles = roles;
        this.enabled = enabled;
        this.likedComments = likedComments;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getUsername() {
        return username;
    }



    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public Collection<Long> getLikedComments() {
        return likedComments;
    }

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.getRole().equals(role));
    }

    public boolean isEnabled() { return enabled; }


}
