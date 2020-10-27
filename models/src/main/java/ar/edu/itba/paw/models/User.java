package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
public class User {

    public static final long DEFAULT_AVATAR_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    private final Long id;

    @Column(name = "creation_date", nullable = false)
    private final LocalDateTime creationDate;

    @Column(nullable = false, unique = true, length = 50)
    private final String username;

    @Column(nullable = false, length = 200)
    private final String password;

    @Column(nullable = false, length = 50)
    private final String name;

    @Column(nullable = false, unique = true, length = 200)
    private final String email;

    @Column(nullable = false, length = 400)
    private final String description;

    @ManyToOne //Entidad Imagen??
    private final long avatarId;

    // Propiedad Computada
    private final long totalLikes;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private final Collection<Role> roles;
    /*
        CREATE TABLE IF NOT EXISTS USER_ROLE
        (
            user_id    INTEGER     NOT NULL,
            role_name  varchar(50) NOT NULL,
            PRIMARY KEY (user_id, role_name),
            FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE,
        );
     */

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

    public User() {

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

    public boolean isValidated() {
        return roles.stream().noneMatch(role -> role.getRole().equals(Role.NOT_VALIDATED_ROLE));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", avatarId=" + avatarId +
                ", totalLikes=" + totalLikes +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
