package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.ForbiddenAccessHandler;
import ar.edu.itba.paw.webapp.auth.JwtAuthFilter;
import ar.edu.itba.paw.webapp.auth.JwtUtil;
import ar.edu.itba.paw.webapp.auth.UnauthorizedRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@EnableWebSecurity
@ComponentScan({ "ar.edu.itba.paw.webapp.auth", })
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetails;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil(@Value("classpath:jwtSecret.key") Resource secretResource) throws IOException {
        return new JwtUtil(secretResource);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
    }

    // Spring Security Unresolved Issues
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                .sessionManagement()
                    // Don't create session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().exceptionHandling()
                    // Executes when unauthorized user is trying to access resource
                    .authenticationEntryPoint(new UnauthorizedRequestHandler())
                    .accessDeniedHandler(new ForbiddenAccessHandler())

                .and().authorizeRequests()

                    // Home Controller
                        // "/" - GET

                    // User Controller
                        // "/users" - GET
                        // "/users/options" - GET
                        // "/users/{id:[\d]+}" - GET
                        // "/users/{id:[\d]+}/avatar" - GET
                        // "/users/{id:[\d]+}/posts" - GET
                        // "/users/{id:[\d]+}/comments" - GET
                        // "/users/{id:[\d]+}/following" - GET
                        // "/users/{id:[\d]+}/following/{userId:[\d]+}" - GET
                        // "/users/{id:[\d]+}/bookmarked" - GET
                        // "/users/{id:[\d]+}/bookmarked/{userId:[\d]+}" - GET
                    .antMatchers(HttpMethod.POST, "/api/users", "/api/users/")
                    .anonymous()

                    .antMatchers(HttpMethod.PUT, "/api/users/{id:[\\d]+}/privilege", "/api/users/{id:[\\d]+}/privilege/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.PUT, "/api/users/{id:[\\d]+}/enabled", "/api/users/{id:[\\d]+}/enabled/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.DELETE, "/api/users/{id:[\\d]+}/enabled", "/api/users/{id:[\\d]+}/enabled/")
                    .hasRole("ADMIN")


                    // Authenticated User Controller
                    .antMatchers(HttpMethod.POST, "/api/user", "/api/user/")
                    .anonymous()

                    .antMatchers("/api/user/refresh_token", "/api/user/refresh_token/")
                    .permitAll()

                    .antMatchers(HttpMethod.POST, "/api/user/email_confirmation", "/api/user/email_confirmation/")
                    .hasRole("NOT_VALIDATED")

                    .antMatchers(HttpMethod.PUT, "/api/user/email_confirmation", "/api/user/email_confirmation/")
                    .hasAnyRole("ANONYMOUS", "NOT_VALIDATED")

                    .antMatchers(HttpMethod.POST, "/api/user/password_reset", "/api/user/password_reset/")
                    .anonymous()

                    .antMatchers(HttpMethod.PUT, "/api/user/password_reset", "/api/user/password_reset/")
                    .anonymous()

                    .antMatchers("/api/user", "/api/user/", "/api/user/**")
                    .authenticated()


                    // Post Controller
                        // "/posts" - GET
                        // "/posts/options" - GET
                        // "/posts/{id:[\d]+}" - GET
                        // "/posts/{id:[\d]+}/votes" - GET
                        // "/posts/{id:[\d]+}/votes/{userId:[\d]+}" - GET
                        // "/posts/{id:[\d]+}/comments" - GET
                        // "/posts/categories" - GET
                    .antMatchers(HttpMethod.POST, "/api/posts", "/api/posts/")
                    .hasRole("USER")

                    .antMatchers(HttpMethod.PUT, "/api/posts/{id:[\\d]+}", "/api/posts/{id:[\\d]+}/")
                    .hasRole("USER")

                    .antMatchers(HttpMethod.PUT, "/api/posts/{id:[\\d]+}/enabled", "/api/posts/{id:[\\d]+}/enabled/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.DELETE, "/api/posts/{id:[\\d]+}/enabled", "/api/posts/{id:[\\d]+}/enabled/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.PUT, "/api/posts/{id:[\\d]+}/votes", "/api/posts/{id:[\\d]+}/votes/")
                    .hasRole("USER")

                    // Movie Controller
                        // "/movies" - GET
                        // "/movies/options" - GET
                        // "/movies/{id:[\\d]+}" - GET
                        // "/movies/{id:[\\d]+}/poster" - GET
                        // "/movies/{id:[\\d]+}/posts" - GET
                    .antMatchers(HttpMethod.POST, "/api/movies", "/api/movies/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.PUT, "/api/movies/{id:[\\d]+}/poster", "/api/movies/{id:[\\d]+}/poster/")
                    .hasRole("ADMIN")


                    // Comment Controller
                        // "/comments" - GET
                        // "/comments/options" - GET
                        // "/comments/{id:[\\d]+}" - GET
                        // "/comments/{id:[\\d]+}/votes" - GET
                        // "/comments/{id:[\\d]+}/votes/{userId:[\\d]+}" - GET
                        // "/comments/{id:[\\d]+}/children" - GET
                    .antMatchers(HttpMethod.POST, "/api/comments", "/api/comments/")
                    .hasRole("USER")

                    .antMatchers(HttpMethod.PUT, "/api/comments/{id:[\\d]+}", "/api/comments/{id:[\\d]+}/")
                    .hasRole("USER")

                    .antMatchers(HttpMethod.PUT, "/api/comments/{id:[\\d]+}/enabled", "/api/comments/{id:[\\d]+}/enabled/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.DELETE, "/api/comments/{id:[\\d]+}/enabled", "/api/comments/{id:[\\d]+}/enabled/")
                    .hasRole("ADMIN")

                    .antMatchers(HttpMethod.PUT, "/api/comments/{id:[\\d]+}/votes", "/api/comments/{id:[\\d]+}/votes/")
                    .hasRole("USER")

                    
                    // Default
                    .antMatchers("/api/**").permitAll()

                .and().csrf().disable()

                // Register Jwt Authentication Filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/resources/**", "/favicon.ico");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
