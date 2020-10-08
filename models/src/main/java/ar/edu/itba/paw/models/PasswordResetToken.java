package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class PasswordResetToken {

    private static final int VALID_DAYS = 1;

    private final long tokenId;
    private final User user;
    private final String token;
    private final LocalDateTime expiryDate;

    public static LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusDays(VALID_DAYS);
    }

    public PasswordResetToken(long tokenId, String token, LocalDateTime expiryDate, User user) {
        this.tokenId = tokenId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
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
