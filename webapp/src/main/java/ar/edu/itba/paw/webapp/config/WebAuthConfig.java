package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Bean
    public AuthenticationManager authManager() throws Exception {
        return this.authenticationManager();
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
    }

    // Spring Security Unresolved Issues
    // TODO: Hay algo mal, te manda a login cuando tenes una cookie invalida (reseteo el server)
    // TODO: Make a redirect, change browser url
    // TODO: Preguntarle a Sotuyo sobre la implementacion de autoLogin
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                .sessionManagement()
                    .invalidSessionUrl("/login")

                .and().authorizeRequests()
                    .antMatchers("/login", "/user/create").anonymous()
                    .antMatchers("/user/{userId:[\\d+]}").permitAll()
                    .antMatchers("/user/**").hasRole("USER")
                    .antMatchers("/post/create").hasRole("USER")
                    .antMatchers("/movie/create").hasRole("ADMIN")
                    .antMatchers("/**").permitAll()

                .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", false)

                .and().rememberMe()
                    .rememberMeParameter("remember-me")
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                    .key(FileCopyUtils.copyToString(new InputStreamReader(rememberMeKeyResource.getInputStream())))

                .and().logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")

                .and().exceptionHandling()
                    .accessDeniedPage("/")

                .and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/resources/**", "/favicon.ico");
    }

}
