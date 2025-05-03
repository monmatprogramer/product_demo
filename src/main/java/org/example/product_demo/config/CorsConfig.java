package org.example.product_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    private static final Logger logger = Logger.getLogger(CorsConfig.class.getName());
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://master.d2ji8l5dbhz3ww.amplifyapp.com",
        "http://localhost:3000"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Configuring CORS mappings...");
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGINS.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "Content-Length", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    @Primary
    public CorsFilter corsFilter() {
        logger.info("Creating CORS filter...");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow specific origins
        ALLOWED_ORIGINS.forEach(config::addAllowedOrigin);
        
        // Allow all methods
        config.addAllowedMethod("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Expose headers to client
        config.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Content-Length", "Content-Disposition",
            "Access-Control-Allow-Origin", "Access-Control-Allow-Methods", 
            "Access-Control-Allow-Headers", "Access-Control-Allow-Credentials"
        ));
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Cache preflight for 1 hour
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}