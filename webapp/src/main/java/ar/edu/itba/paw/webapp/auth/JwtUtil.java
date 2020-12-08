package ar.edu.itba.paw.webapp.auth;

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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUtil {

    private static final int HOUR_MILLIS = 1000 * 60 * 60;

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

            final String username = body.getSubject();

            final String password = body.get("password", String.class);

            final boolean enabled = Boolean.parseBoolean(body.get("enabled", String.class));

            final Collection<GrantedAuthority> roles =
                    deserializeRolesToGrantedAuthorities(body.get("roles", String.class));

            return new org.springframework.security.core.userdetails.User(username, password, enabled,
                    false, false, false, roles);

        } catch (JwtException | ClassCastException e) {
            return null;
        }
    }

    public String generateToken(User u) {

        Claims claims = Jwts.claims();

        claims.setSubject(u.getUsername());
        claims.put("password", u.getPassword());
        claims.put("enabled", String.valueOf(u.isEnabled()));
        claims.put("roles", serializeRoles(u.getRoles()));

        return "Bearer " +
                Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 12 * HOUR_MILLIS))
                .signWith(secret)
                .compact();
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
