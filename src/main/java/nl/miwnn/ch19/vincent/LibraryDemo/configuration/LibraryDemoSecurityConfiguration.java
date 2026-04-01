package nl.miwnn.ch19.vincent.LibraryDemo.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

/**
 * @author Vincent Velthuizen
 * Configure the security for the Library Demo
 */
@Configuration
@EnableWebSecurity
public class LibraryDemoSecurityConfiguration {
    private static final Logger log = LoggerFactory.getLogger(LibraryDemoSecurityConfiguration.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/book/all",
                                "/book/detail/**",
                                "/author/all",
                                "/images/**",
                                "/css/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/book/edit/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/book/all")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/book/all")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
