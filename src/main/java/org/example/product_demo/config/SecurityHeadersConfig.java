package org.example.product_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {

                // Add security headers to prevent common attacks
                response.setHeader("X-XSS-Protection", "1; mode=block");
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");

                // Content Security Policy
                response.setHeader("Content-Security-Policy",
                        "default-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self'; script-src 'self'");

                // Strict Transport Security (HSTS)
                // Uncomment in production with HTTPS
                // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                filterChain.doFilter(request, response);
            }
        };
    }
}