package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class MoovifyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword(), getGrantedAuthorities(user.getRoles())
        );
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map((role) -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
    }
}
