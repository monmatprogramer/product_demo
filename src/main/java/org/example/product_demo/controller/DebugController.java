package org.example.product_demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DebugController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/encode")
    public ResponseEntity<?> encodePassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        return ResponseEntity.ok(Map.of(
                "original", password,
                "encoded", encoded
        ));
    }

    @GetMapping("/match")
    public ResponseEntity<?> matchPasswords(
            @RequestParam String rawPassword,
            @RequestParam String encodedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        return ResponseEntity.ok(Map.of(
                "matches", matches
        ));
    }
}