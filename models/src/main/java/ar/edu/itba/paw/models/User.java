package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    public static final String TABLE_NAME = "users";
    public static final String USER_ROLE_TABLE_NAME = "user_role";
    public static final String USER_FAV_POST = "user_fav_post";
    public static final String USERS_FOLLOWS = "users_follows";

    public static final long DEFAULT_AVATAR_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
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

    @Column(nullable = false, length = 15)
    @Basic(optional = false)
    private String language;

    @OneToOne(optional = true, fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", referencedColumnName = "image_id")
    private Image avatar;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<PostVote> postVotes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<CommentVote> commentVotes;

    @Transient
    private Long totalLikes;

    @Transient
    private Integer followerCount;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = USER_ROLE_TABLE_NAME,
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Post> posts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_follows",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_follow_id")
    )
    private Set<User> following;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "following")
    private Set<User> followers;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = USER_FAV_POST,
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> bookmarkedPosts;

    @Column(nullable = false)
    private boolean enabled;

    public User(long id, LocalDateTime creationDate, String username, String password, String name, String email, String description, String language, Image avatar, Set<Role> roles, boolean enabled, Set<PostVote> postVotes, Set<CommentVote> commentVotes, Set<Post> posts, Set<Comment> comments, Set<User> following, Set<User> followers, Set<Post> bookmarkedPosts) {
        this(creationDate, username, password, name, email, description, language, avatar, roles, enabled, postVotes, commentVotes, posts, comments, following, followers, bookmarkedPosts);
        this.id = id;
    }

    public User(LocalDateTime creationDate, String username, String password, String name, String email, String description, String language, Image avatar, Set<Role> roles, boolean enabled, Set<PostVote> postVotes, Set<CommentVote> commentVotes, Set<Post> posts, Set<Comment> comments, Set<User> following, Set<User> followers, Set<Post> bookmarkedPosts) {
        this.creationDate = creationDate;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.description = description;
        this.language = language;
        this.avatar = avatar;
        this.roles = roles;
        this.enabled = enabled;
        this.postVotes = postVotes;
        this.commentVotes = commentVotes;
        this.posts = posts;
        this.comments = comments;
        this.following = following;
        this.followers = followers;
        this.bookmarkedPosts = bookmarkedPosts;
    }

    protected User() {
        //JPA
    }

    public void calculateTotalLikes() {
        if(totalLikes == null) {
            final long totalPostLikes = posts.stream()
                    .reduce(0L, (acum, post) -> acum += post.getTotalVotes(), Long::sum);

            final long totalCommentLikes = comments.stream()
                    .reduce(0L, (acum, comment) -> acum += comment.getTotalVotes(), Long::sum);

            totalLikes = totalPostLikes + totalCommentLikes;
        }
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

    public String getLanguage() {
        return language;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        getRoles().add(role);
    }

    public void removeRole(Role role) {
        getRoles().removeIf(r -> r.equals(role));
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public long getTotalLikes() {
        if(totalLikes == null)
            calculateTotalLikes();

        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getFollowerCount() {
        if(followerCount == null)
            followerCount = getFollowers().size();

        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public Collection<CommentVote> getCommentLikes() {
        return commentVotes;
    }

    public void removeCommentLike(CommentVote like) {
        getCommentLikes().remove(like);
    }

    public void addCommentLike(CommentVote like) {
        getCommentLikes().add(like);
    }

    public Collection<PostVote> getPostLikes() {
        return postVotes;
    }

    public void removePostLike(PostVote like) {
        getPostLikes().remove(like);
    }

    public void addPostLike(PostVote like) {
        getPostLikes().add(like);
    }

    public boolean hasRole(Role role) {
        return getRoles().stream().anyMatch(r -> r.equals(role));
    }

    public Collection<Post> getBookmarkedPosts() {
        return bookmarkedPosts;
    }

    public boolean isPostBookmarked(Post post) {
        return bookmarkedPosts.contains(post);
    }

    public void bookmarkPost(Post post) {
        bookmarkedPosts.add(post);
        post.getBookmarkedBy().add(this);
    }

    public void unbookmarkPost(Post post) {
        bookmarkedPosts.remove(post);
        post.getBookmarkedBy().remove(this);
    }

    public Collection<User> getFollowingUsers() {
        return following;
    }

    public void followUser(User user) {
        getFollowingUsers().add(user);
        user.getFollowers().add(this);
    }

    public void unfollowUser(User user) {
        getFollowingUsers().remove(user);
        user.getFollowers().remove(this);
    }

    public boolean isUserFollowing(User user) {
        return getFollowingUsers().contains(user);
    }

    public boolean isUserFollowing(String username) {
        return getFollowingUsers().stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public Collection<User> getFollowers() {
        return followers;
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

    public String getAvatarType() {
        if(avatar == null)
            return Image.DEFAULT_TYPE;

        return avatar.getType();
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public boolean isValidated() {
        return hasRole(Role.USER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
