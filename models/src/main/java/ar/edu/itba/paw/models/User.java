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


    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;


    @Column(nullable = false, unique = true, length = 50)
    @Basic(optional = false)
    private String username;


    @Column(nullable = false, length = 200)
    @Basic(optional = false)
    private String password;


    @Column(nullable = false, length = 50)
    @Basic(optional = false)
    private String name;


    @Column(nullable = false, unique = true, length = 200)
    @Basic(optional = false)
    private String email;


    @Column(nullable = false, length = 400)
    @Basic(optional = false)
    private String description;

    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar_id")
    private Image avatar;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Collection<PostLike> postLikes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Collection<CommentLike> commentLikes;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<Role> roles;

    @Column(nullable = false)
    private boolean enabled;

    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, String description, Image avatar, Collection<Role> roles, boolean enabled, Collection<PostLike> postLikes, Collection<CommentLike> commentLikes) {
        this(creationDate, username, password, name, email, description, avatar, roles, enabled, postLikes, commentLikes);
        this.id = id;
    }

    public User(LocalDateTime creationDate, String username, String password, String name, String email, String description, Image avatar, Collection<Role> roles, boolean enabled, Collection<PostLike> postLikes, Collection<CommentLike> commentLikes) {
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.description = description;
        this.avatar = avatar;
        this.roles = roles;
        this.enabled = enabled;
        this.postLikes = postLikes;
        this.commentLikes = commentLikes;
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

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getAvatarId() {
        if(avatar == null)
            return DEFAULT_AVATAR_ID;

        return avatar.getId();
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public long getTotalPostLikes() {
        return postLikes.stream().reduce(0, (acum, postLike) -> acum += postLike.getValue(), Integer::sum);
    }

    public long getTotalCommentLikes() {
        return commentLikes.stream().reduce(0, (acum, commentLike) -> acum += commentLike.getValue(), Integer::sum);
    }

    public long getTotalLikes() {
        return getTotalPostLikes() + getTotalPostLikes();
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
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
