package com.learn.lms.service;

import com.learn.lms.dto.UserDTO;
import com.learn.lms.model.Role;
import com.learn.lms.model.RoleType;
import com.learn.lms.model.User;
import com.learn.lms.repository.RoleRepository;
import com.learn.lms.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();

        this.userRepository.findByUsername(userDTO.getUsername()).ifPresent(existing -> {
            throw new IllegalArgumentException("Username already exists");
        });

        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        RoleType roleType = RoleType.valueOf(userDTO.getRole().toUpperCase());
        Role role = this.roleRepository.findByName(roleType).orElseThrow(() ->
            new IllegalArgumentException("Role not found")
        );
        user.setRole(role);

        return userRepository.save(user);
    }
}
