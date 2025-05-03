package org.example.product_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public String test() {
        return "Application is running";
    }

    @GetMapping("/testdb")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "checking");

        try {
            if (jdbcTemplate != null) {
                String dbStatus = jdbcTemplate.queryForObject("SELECT 'connected' FROM DUAL", String.class);
                result.put("database", dbStatus);
                result.put("status", "success");
            } else {
                result.put("database", "JdbcTemplate not autowired");
                result.put("status", "error");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            result.put("exception", e.getClass().getName());
        }

        return result;
    }
}