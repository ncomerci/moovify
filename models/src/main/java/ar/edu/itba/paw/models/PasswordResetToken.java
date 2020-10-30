package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    private static final int VALID_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_token_id_seq")
    @SequenceGenerator(sequenceName = "password_reset_token_token_id_seq", name = "password_reset_token_token_id_seq", allocationSize = 1)
    @Column(name ="token_id")
    private Long tokenId;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    @Basic(optional = false)
    private String token;

    @Column(name = "expiry", nullable = false)
    @Basic(optional = false)
    private LocalDateTime expiryDate;

    public static LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusDays(VALID_DAYS);
    }

    public PasswordResetToken(long tokenId, String token, LocalDateTime expiryDate, User user) {
        this(token, expiryDate, user);
        this.tokenId = tokenId;
    }

    public PasswordResetToken(String token, LocalDateTime expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    protected PasswordResetToken() {
        //Hibernate
    }

    public long getTokenId() {
        return tokenId;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void resetExpiryDate() {
        expiryDate = calculateExpiryDate();
    }

    public boolean isValid() {
        return expiryDate.compareTo(LocalDateTime.now()) >= 0;
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "tokenId=" + tokenId +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
