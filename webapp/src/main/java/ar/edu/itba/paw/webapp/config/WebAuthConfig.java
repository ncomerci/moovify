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
    // TODO: Cambiar URL cuando sucede un redirect por autenticacion
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                .sessionManagement()
                    //.invalidSessionUrl("/")

                .and().authorizeRequests()

                    // Home Controller
                        // "/"
                    .antMatchers("/admin/**").hasRole("ADMIN")

                    // User Controller
                        // "/user/{userId:[\d]+}
                    .antMatchers("/login", "/user/create").anonymous()
                    .antMatchers("/user/profile").authenticated()
                    .antMatchers(
                            "/user/registrationConfirm",
                            "/user/resendConfirmation").hasRole("NOT_VALIDATED")
                    .antMatchers(
                            "/user/resetPassword",
                            "/user/updatePassword/token",
                            "/user/updatePassword").anonymous()
                    .antMatchers(HttpMethod.POST,"/user/promote/{id:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/user/delete/{id:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/user/restore/{id:[\\d]+}").hasRole("ADMIN")

                    // Post Controller
                        // "/post/{postId}"
                    .antMatchers("/post/create").hasRole("USER")
                    .antMatchers(HttpMethod.POST,"/post/delete/{postId:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/post/restore/{postId:[\\d]+}").hasRole("ADMIN")

                    // Movie Controller
                        // "/movies/{movieId}
                    .antMatchers("/movie/create", "/movie/register").hasRole("ADMIN")

                    // Comment Controller
                    .antMatchers("/comment/create").hasRole("USER")
                    .antMatchers(HttpMethod.POST, "/comment/like").hasRole("USER")
                    .antMatchers(HttpMethod.POST,"/comment/delete/{commentId:[\\d]+}").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST,"/comment/restore/{commentId:[\\d]+}").hasRole("ADMIN")
                        // "/comment/{commentId:[\\d]+}"
                    // Search Controller
                        // "/search/posts"
                        // "/search/movies"
                        // "/search/users"

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
