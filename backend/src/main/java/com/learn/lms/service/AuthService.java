package com.learn.lms.service;

import com.learn.lms.config.JwtUtils;
import com.learn.lms.model.AuthResponse;
import com.learn.lms.model.Role;
import com.learn.lms.model.RoleType;
import com.learn.lms.model.User;
import com.learn.lms.repository.AuthRepository;
import com.learn.lms.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AuthRepository authRepository, RoleRepository roleRepository, JwtUtils jwtService) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse authenticate(String username, String password) {
        var user = authRepository.findByUsername(username);

        // passwordEncoder.matches(raw, encoded) if you use BCrypt
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtService.generateToken(username, user.getRole().getName().name());
            return new AuthResponse(token, username, user.getRole().getName().name());
        }
        return null;
    }

    public boolean register(String username, String password, String email, String role) {
        if (authRepository.findByUsername(username) != null) return false;

        Role roleEntity = this.roleRepository.findByName(RoleType.valueOf(role.toUpperCase())).orElseThrow(() ->
            new IllegalArgumentException("Invalid role: " + role)
        );

        var newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);

        newUser.setRole(roleEntity);

        authRepository.save(newUser);
        return true;
    }
}
