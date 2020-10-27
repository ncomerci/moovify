package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "users")
public class User {

    public static final long DEFAULT_AVATAR_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    private Long id;

    @Basic(optional = false)
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Basic(optional = false)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Basic(optional = false)
    @Column(nullable = false, length = 200)
    private String password;

    @Basic(optional = false)
    @Column(nullable = false, length = 50)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Basic(optional = false)
    @Column(nullable = false, length = 400)
    private String description;

    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar_id")
    private Image avatar;

    // TODO esperamos a Post y Comments
    private long totalLikes;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Collection<Role> roles;

    @Column(nullable = false)
    private boolean enabled;

    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, String description, Image avatar, long totalLikes, Collection<Role> roles, boolean enabled) {
        this(creationDate, username, password, name, email, description, avatar, totalLikes, roles, enabled);
        this.id = id;
    }

    public User(LocalDateTime creationDate, String username, String password, String name, String email, String description, Image avatar, long totalLikes, Collection<Role> roles, boolean enabled) {
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.description = description;
        this.avatar = avatar;
        this.totalLikes = totalLikes;
        this.roles = roles;
        this.enabled = enabled;
    }

    protected User() {
        //JPA
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

    public boolean hasRole(Role role) {
        return roles.stream().anyMatch(r -> r.equals(role));
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
        if(avatar == null)
            return DEFAULT_AVATAR_ID;

        return avatar.getId();
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public boolean isValidated() {
        return hasRole(Role.USER);
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
                ", avatarId=" + avatar.getId() +
                ", totalLikes=" + totalLikes +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
