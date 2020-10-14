package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class UserVerificationToken {

    private static final int VALID_DAYS = 1;

    private final long id;
    private final String token;
    private final User user;
    private final LocalDateTime expiryDate;

    public static LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusDays(VALID_DAYS);
    }

    public UserVerificationToken(Long id, String token, LocalDateTime expiryDate, User user) {
        this.id = id;
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public Long getId() {
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
