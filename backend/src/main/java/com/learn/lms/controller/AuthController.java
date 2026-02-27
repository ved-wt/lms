package com.learn.lms.controller;

import com.learn.lms.model.AuthResponse;
import com.learn.lms.service.AuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        AuthResponse response = authService.authenticate(credentials.get("username"), credentials.get("password"));
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userDetails) {
        boolean isRegistered = authService.register(
            userDetails.get("username"),
            userDetails.get("password"),
            userDetails.get("email"),
            userDetails.get("role")
        );

        if (isRegistered) {
            return ResponseEntity.ok(Map.of("message", "Registration successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }
    }
}
