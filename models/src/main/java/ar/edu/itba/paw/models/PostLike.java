package ar.edu.itba.paw.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "posts_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostLike {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostLike.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_likes_post_likes_id_seq")
    @SequenceGenerator(sequenceName = "posts_likes_post_likes_id_seq", name = "posts_likes_post_likes_id_seq", allocationSize = 1)
    @Column(name = "post_likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private int value;

    public PostLike(User user, Post post, int value) {
        this.user = user;
        this.post = post;
        this.value = value;
    }

    protected PostLike() {
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if(value == 0) {
            LOGGER.error("Tried to assign value 0 to {}", this);
            return;
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return "PostLikes{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", post=" + post.getId() +
                ", value=" + value +
                '}';
    }
}
