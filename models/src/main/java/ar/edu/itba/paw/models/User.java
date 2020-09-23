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
    private final Collection<Role> roles;

    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, Collection<Role> roles) {
        this.id = id;
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.roles = roles;
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

    public Collection<Role> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.getRole().equals(role));
    }
}
