package com.learn.lms.controller;

import com.learn.lms.model.User;
import com.learn.lms.repository.UserRepository;
import com.learn.lms.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    public ProgressController(ProgressService progressService, UserRepository userRepository) {
        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    @PostMapping("/complete/{lessonId}")
    public ResponseEntity<?> completeLesson(@PathVariable Long lessonId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        progressService.markAsCompleted(user.getId(), lessonId);

        return ResponseEntity.ok().body("{\"message\": \"Progress saved\"}");
    }
}
