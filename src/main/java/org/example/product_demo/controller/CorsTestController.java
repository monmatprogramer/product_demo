package org.example.product_demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/cors-test")
public class CorsTestController {

    private static final Logger logger = Logger.getLogger(CorsTestController.class.getName());

    @GetMapping
    public ResponseEntity<?> testCors(HttpServletRequest request) {
        logger.info("CORS test endpoint called");
        
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String host = request.getHeader("Host");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "CORS is working correctly");
        response.put("requestHeaders", Map.of(
            "Origin", origin != null ? origin : "null",
            "Referer", referer != null ? referer : "null",
            "Host", host != null ? host : "null"
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<?> testCorsPOST(@RequestBody(required = false) Map<String, Object> body, 
                                         HttpServletRequest request) {
        logger.info("CORS test POST endpoint called");
        
        String origin = request.getHeader("Origin");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "CORS POST is working correctly");
        response.put("origin", origin != null ? origin : "null");
        response.put("receivedData", body != null ? body : Map.of());
        
        return ResponseEntity.ok(response);
    }
}