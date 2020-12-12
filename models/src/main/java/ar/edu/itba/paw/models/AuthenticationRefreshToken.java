package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "authentication_refresh_token")
public class AuthenticationRefreshToken {

    public static final String TABLE_NAME = "authentication_refresh_token";

    private static final int VALID_DAYS = 15;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authentication_refresh_token_token_id_seq")
    @SequenceGenerator(sequenceName = "authentication_refresh_token_token_id_seq", name = "authentication_refresh_token_token_id_seq", allocationSize = 1)
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

    public static String generateSecureToken() {
        final byte[] secureBytes = new byte[64];

        secureRandom.nextBytes(secureBytes);

        return base64Encoder.encodeToString(secureBytes);
    }

    public AuthenticationRefreshToken(long id, String token, LocalDateTime expiryDate, User user) {
        this(token, expiryDate, user);
        this.id = id;
    }

    public AuthenticationRefreshToken(String token, LocalDateTime expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public AuthenticationRefreshToken() {
        //Hibernate
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public boolean isValid() {
        return expiryDate.compareTo(LocalDateTime.now()) >= 0;
    }

    public void resetExpiryDate() {
        expiryDate = calculateExpiryDate();
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void reset() {
        setToken(generateSecureToken());
        resetExpiryDate();
    }

    @Override
    public String toString() {
        return "AuthenticationRefreshToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + user +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
