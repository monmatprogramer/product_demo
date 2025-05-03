package org.example.product_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
public class SecurityHeadersConfig {

    private static final Logger logger = Logger.getLogger(SecurityHeadersConfig.class.getName());

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

                // CORS Headers - Ensure they're set for every response
                response.setHeader("Access-Control-Allow-Origin", getOriginHeader(request));
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, X-Requested-With");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Max-Age", "3600");

                // Handle preflight OPTIONS requests
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }

                // Modified Content Security Policy to allow mixed content (for development)
                // Note: In production, this should be more restrictive
                response.setHeader("Content-Security-Policy",
                        "default-src * 'unsafe-inline' 'unsafe-eval'; connect-src *; img-src * data:; style-src * 'unsafe-inline';");

                // Strict Transport Security (HSTS) - commented out for now since you're using HTTP
                // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                filterChain.doFilter(request, response);
            }
            
            private String getOriginHeader(HttpServletRequest request) {
                String origin = request.getHeader("Origin");
                if (origin != null && (origin.contains("localhost") || origin.contains("amplifyapp.com"))) {
                    logger.info("Allowing origin: " + origin);
                    return origin;
                }
                logger.info("Using default origin: https://master.d2ji8l5dbhz3ww.amplifyapp.com");
                return "https://master.d2ji8l5dbhz3ww.amplifyapp.com";
            }
        };
    }
}