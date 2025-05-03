package org.example.product_demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DatabaseMigrationRunner implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DatabaseMigrationRunner.class.getName());
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        logger.info("Running database migration checks...");
        try {
            String result = jdbcTemplate.queryForObject("SELECT 'DB Connection Test Successful' FROM DUAL", String.class);
            logger.info(result);
        } catch (Exception e) {
            logger.severe("Database connection failed: " + e.getMessage());
            // Don't throw the error to prevent application startup failure
        }
    }

    public static void main(String[] args) {
        // Placeholder for the container command reference
        System.out.println("Database migration runner main method called");
    }
}