package org.example.product_demo.controller;

import org.example.product_demo.exception.ApiException;
import org.example.product_demo.exception.InvalidPasswordException;
import org.example.product_demo.model.User;
import org.example.product_demo.model.UserRole;
import org.example.product_demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserAdminController {

    private static final Logger logger = Logger.getLogger(UserAdminController.class.getName());
    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest request) {
        logger.info("Admin creating new user: " + request.getUsername());

        try {
            // Validate that passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new InvalidPasswordException("Passwords do not match");
            }

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword());
            newUser.setEmail(request.getEmail() != null ? request.getEmail() : "");
            newUser.setRole(request.isAdmin() ? UserRole.ADMIN : UserRole.USER);

            User createdUser = userService.createUserByAdmin(newUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User created successfully",
                            "username", createdUser.getUsername(),
                            "role", createdUser.getRole().name()
                    ));
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.severe("Unexpected error adding user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    // Inner class for add user request
    public static class AddUserRequest {
        private String username;
        private String password;
        private String confirmPassword;
        private String email;
        private boolean admin;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public boolean isAdmin() { return admin; }
        public void setAdmin(boolean admin) { this.admin = admin; }
    }
}