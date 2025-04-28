package org.example.product_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // disable CSRF for simplicity in dev (you may want it in prod)
                .csrf(csrf -> csrf.disable())

                // declare which requests are allowed without authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()   // allow all calls under /api/
                        .anyRequest().authenticated()             // everything else still secured
                )

                // you can leave the default login form enabled, or disable formLogin if you never need it:
                .httpBasic(Customizer.withDefaults());     // you can also do .formLogin(Customizer.withDefaults())

        return http.build();
    }
}
