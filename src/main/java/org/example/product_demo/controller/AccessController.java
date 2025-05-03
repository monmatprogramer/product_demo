package org.example.product_demo.controller;

import org.example.product_demo.model.User;
import org.example.product_demo.security.JwtUtils;
import org.example.product_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/access")
@CrossOrigin(origins = {"https://master.d2ji8l5dbhz3ww.amplifyapp.com", "http://localhost:3000"})
public class AccessController {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Autowired
    public AccessController(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {

            User user = (User) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(Map.of("message", "No authenticated user found"));
    }

    @GetMapping("/check-admin")
    public ResponseEntity<?> checkAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            return ResponseEntity.ok(Map.of("isAdmin", isAdmin));
        }

        return ResponseEntity.ok(Map.of("isAdmin", false));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Token is required"));
        }

        boolean isValid = jwtUtils.validateToken(token);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        if (isValid) {
            String username = jwtUtils.extractUsername(token);
            Long userId = jwtUtils.getUserIdFromToken(token);
            String role = jwtUtils.getUserRoleFromToken(token);

            response.put("username", username);
            response.put("userId", userId);
            response.put("role", role);
        }

        return ResponseEntity.ok(response);
    }
}