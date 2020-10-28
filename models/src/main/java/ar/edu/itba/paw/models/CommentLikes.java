package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "comments_likes")
public class CommentLikes {

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

    public CommentLikes(User user, Comment comment, int value) {
        this.user = user;
        this.comment = comment;
        this.value = value;
    }

    protected CommentLikes() {
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
}
