package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class UserVerificationToken {

    private final long id;
    private final String token;
    private final User user;
    private final LocalDateTime expiryTimestamp;

    public static LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusDays(1);
    }

    public UserVerificationToken(Long id, String token, LocalDateTime expiryTimestamp, User user) {
        this.id = id;
        this.token = token;
        this.expiryTimestamp = expiryTimestamp;
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
        return expiryTimestamp.compareTo(LocalDateTime.now()) >= 0;
    }
}
