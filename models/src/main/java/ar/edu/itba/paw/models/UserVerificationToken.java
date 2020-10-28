package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_verification_token")
public class UserVerificationToken {

    private static final int VALID_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_verification_token_token_id_seq")
    @SequenceGenerator(sequenceName = "user_verification_token_token_id_seq", name = "user_verification_token_token_id_seq", allocationSize = 1)
    @Column(name = "token_id")
    private Long id;

    @Column(nullable = false, unique = true)
    @Basic(optional = false)
    private String token;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry", nullable = false)
    @Basic(optional = false)
    private LocalDateTime expiryDate;

    public static LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusDays(VALID_DAYS);
    }

    public UserVerificationToken(long id, String token, LocalDateTime expiryDate, User user) {
        this(token, expiryDate, user);
        this.id = id;
    }

    public UserVerificationToken(String token, LocalDateTime expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public UserVerificationToken() {
        //Hibernate
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public boolean isValid() {
        return expiryDate.compareTo(LocalDateTime.now()) >= 0;
    }

    @Override
    public String toString() {
        return "UserVerificationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + user +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
