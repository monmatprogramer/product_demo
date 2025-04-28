package org.example.product_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/debug/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> {})
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(Collections.singletonList(provider));
    }

    // Remove the passwordEncoder bean from here
}