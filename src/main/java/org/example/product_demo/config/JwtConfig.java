package org.example.product_demo.config;

import org.example.product_demo.repository.RefreshTokenRepository;
import org.example.product_demo.repository.UserRepository;
import org.example.product_demo.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // Default 24 hours in milliseconds
    private long jwtExpiration;

    @Value("${jwt.refreshExpiration:604800000}") // Default 7 days in milliseconds
    private long refreshExpiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public String jwtSecret() {
        // If no secret is configured, generate a secure random one
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[64]; // 512 bits
            random.nextBytes(bytes);
            jwtSecret = Base64.getEncoder().encodeToString(bytes);
        }
        return jwtSecret;
    }

    @Bean
    public long jwtExpiration() {
        return jwtExpiration;
    }

    @Bean
    public long refreshExpiration() {
        return refreshExpiration;
    }

    @Bean
    public RefreshTokenService refreshTokenService() {
        return new RefreshTokenService(refreshExpiration, refreshTokenRepository, userRepository);
    }
}