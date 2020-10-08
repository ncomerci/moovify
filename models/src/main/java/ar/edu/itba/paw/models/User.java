package ar.edu.itba.paw.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class User {

    public static final long DEFAULT_AVATAR_ID = 0;

    private final long id;
    private final LocalDateTime creationDate;
    private final String username;
    private final String password;
    private final String name;
    private final String email;
    private final String description;
    private final long avatarId;
    private final long totalLikes;
    private final Collection<Role> roles;
    private final boolean enabled;


    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, String description, Long avatarId, long totalLikes, Collection<Role> roles, boolean enabled) {
        this.id = id;
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.description = description;
        this.avatarId = (avatarId == null)? DEFAULT_AVATAR_ID : avatarId;
        this.totalLikes = totalLikes;
        this.roles = roles;
        this.enabled = enabled;
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

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.getRole().equals(role));
    }


    public Duration getTimeSinceCreation() {
        return Duration.between(creationDate, LocalDateTime.now());
    }

    public long getDaysSinceCreation() {
        return getTimeSinceCreation().toDays();
    }

    public long getHoursSinceCreation() {
        return getTimeSinceCreation().toHours();
    }

    public long getMinutesSinceCreation() {
        return getTimeSinceCreation().toMinutes();
    }

    public boolean isEnabled() { return enabled; }

    public long getAvatarId() {
        return avatarId;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public boolean isAdmin() {
        return roles.stream().anyMatch(role -> role.getRole().equals(Role.ADMIN_ROLE));
    }
}
