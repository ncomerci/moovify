package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.AuthenticationRefreshToken;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.FileCopyUtils;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUtil {

    private static final int EXPIRATION_TIME_MILLIS =  60 * 1000; // 15 minutes

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    final private Key secret;

    public JwtUtil(Resource secretResource) throws IOException {
        secret = Keys.hmacShaKeyFor(
                FileCopyUtils.copyToString(new InputStreamReader(secretResource.getInputStream()))
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public UserDetails parseToken(String jws) {
        try {

            final Claims body = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();

            // Jwt Expired
            if(new Date().after(body.getExpiration()))
                return null;

            final String username = body.getSubject();

            final boolean enabled = Boolean.parseBoolean(body.get("enabled", String.class));

            final Collection<GrantedAuthority> roles =
                    deserializeRolesToGrantedAuthorities(body.get("roles", String.class));

            return new org.springframework.security.core.userdetails.User(username, "", enabled,
                    false, false, false, roles);

        } catch (JwtException | ClassCastException e) {
            return null;
        }
    }

    public String generateToken(User u) {

        Claims claims = Jwts.claims();

        claims.setSubject(u.getUsername());
        claims.put("enabled", String.valueOf(u.isEnabled()));
        claims.put("roles", serializeRoles(u.getRoles()));

        return "Bearer " +
                Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILLIS))
                .signWith(secret)
                .compact();
    }

    public NewCookie generateRefreshCookie(AuthenticationRefreshToken token) {
        return new NewCookie(REFRESH_TOKEN_COOKIE_NAME,
                token.getToken(),
                "/",
                "127.0.0.1",
                Cookie.DEFAULT_VERSION,
                "Authentication Refresh Token",
                (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), token.getExpiryDate()),
                null,
                false,
                true);
    }

    public NewCookie getDeleteRefreshCookie() {
        return new NewCookie(REFRESH_TOKEN_COOKIE_NAME,
                null,
                "/",
                null,
                Cookie.DEFAULT_VERSION,
                null,
                0,
                new Date(0),
                false,
                true);
    }

    private String serializeRoles(Collection<Role> roles) {
        final StringBuilder sb = new StringBuilder();

        for(Role role : roles)
            sb.append(role.name()).append(' ');

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private Collection<GrantedAuthority> deserializeRolesToGrantedAuthorities(String roles) {
        return Arrays.stream(roles.split(" "))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
