package com.learn.lms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.lms.model.User;
import com.learn.lms.model.UserDTO;
import com.learn.lms.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")

public class UserController {
    // @Autowired
    // private UserService userService;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> all() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("getUser")
    public User getById(@RequestParam Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserDTO user) {
        User savedUser = userService.createUser(user);
        if (savedUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

    }
}
