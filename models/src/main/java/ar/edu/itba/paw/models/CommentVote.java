package ar.edu.itba.paw.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "comments_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"}))
public class CommentVote {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentVote.class);

    public static final String TABLE_NAME = "comments_likes";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_likes_comments_likes_id_seq")
    @SequenceGenerator(sequenceName = "comments_likes_comments_likes_id_seq", name = "comments_likes_comments_likes_id_seq", allocationSize = 1)
    @Column(name = "comments_likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="comment_id", nullable = false)
    private Comment comment;

    @Column(nullable = false)
    private int value;

    public CommentVote(User user, Comment comment, int value) {
        this.user = user;
        this.comment = comment;
        this.value = value;
    }

    protected CommentVote() {
        // Hibernate
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentVote that = (CommentVote) o;
        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CommentLike{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
