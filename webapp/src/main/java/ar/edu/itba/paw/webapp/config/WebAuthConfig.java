package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@ComponentScan({ "ar.edu.itba.paw.webapp.auth", })
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Value("classpath:rememberMe.key")
    private Resource rememberMeKeyResource;

    @Autowired
    private UserDetailsService userDetails;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                    //.invalidSessionUrl("/")

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
                                            "/user/profile/edit",
                                            "/user/changePassword").authenticated()
                    .antMatchers(HttpMethod.POST,
                             "/user/edit/name",
                                        "/user/edit/username",
                                        "/user/edit/description",
                                        "/user/profile/avatar").authenticated()
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

                .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", false)

                .and().rememberMe()
                    .userDetailsService(userDetails)
                    .rememberMeParameter("remember-me")
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                    .key(FileCopyUtils.copyToString(new InputStreamReader(rememberMeKeyResource.getInputStream())))

                .and().logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")

                .and().exceptionHandling()
                    .accessDeniedPage("/403")

                .and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/resources/**", "/favicon.ico");
    }

}
