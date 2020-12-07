package ar.edu.itba.paw.webapp.config;

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
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().exceptionHandling()
                    .authenticationEntryPoint(new UnauthorizedRequestHandler())

                .and().authorizeRequests()

                    // Home Controller
                        // "/"
                        // "/403"

                    // User Controller
                        // "/user/{userId:[\d]+}
                        // "/user/{userId:[\d]+}/posts
                        // "/user/{userId:[\d]+}/comments
                        // /user/avatar/{avatarId:[\d]+}
                        // /user/registrationConfirm
                    .antMatchers("/login", "/user/create").anonymous()
                    .antMatchers("/user/profile",
                                            "/user/profile/posts",
                                            "/user/profile/comments",
                                            "/user/profile/followed/users",
                                            "/user/profile/edit",
                                            "/user/changePassword").authenticated()
                    .antMatchers(HttpMethod.POST,
                            "/user/edit/name",
                            "/user/edit/username",
                            "/user/edit/description",
                            "/user/profile/avatar",
                            "/user/follow/{userId:[\\d]+}",
                            "/user/unfollow/{userId:[\\d]+}",
                            "/user/favourite/posts/add",
                            "/user/favourite/posts/remove"
                    ).authenticated()


                    .antMatchers("/user/resendConfirmation").hasRole("NOT_VALIDATED")
                    .antMatchers(
                            "/user/resetPassword",
                            "/user/updatePassword/token",
                            "/user/updatePassword").anonymous()

                    // Post Controller
                        // "/post/{postId}"
                    .antMatchers("/post/create").hasRole("USER")
                    .antMatchers("/post/edit/{postId:[\\d]+}").hasRole("USER")
                    .antMatchers(HttpMethod.POST, "/post/like").hasRole("USER")

                    // Movie Controller
                        // "/movies/{movieId}
                    .antMatchers("/movie/create").hasRole("ADMIN")
                    .antMatchers("/movie/{movieId:[\\d]+}/poster/update").hasRole("ADMIN")

                    // Comment Controller
                        // "/comment/{commentId:[\\d]+}"
                    .antMatchers(HttpMethod.POST, "/comment/create").hasRole("USER")
                    .antMatchers(HttpMethod.POST, "/comment/like").hasRole("USER")

                    // Search Controller
                        // "/search/posts"
                        // "/search/movies"
                        // "/search/users"

                    // Admin Controller
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/comment/delete/{commentId:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/comment/restore/{commentId:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/post/delete/{postId:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/post/restore/{postId:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/user/promote/{id:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/user/delete/{id:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/user/restore/{id:[\\d]+}").hasRole("ADMIN")

                    // Default
                    .antMatchers("/**").permitAll()

                .and().csrf().disable()

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
