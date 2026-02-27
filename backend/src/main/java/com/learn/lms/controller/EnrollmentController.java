package com.learn.lms.controller;

import com.learn.lms.model.Enrollment;
import com.learn.lms.model.User;
import com.learn.lms.repository.UserRepository;
import com.learn.lms.service.EnrollmentService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;
    private Logger log = LoggerFactory.getLogger(EnrollmentController.class);

    public EnrollmentController(UserRepository userRepository, EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
    }

    // CREATE ENROLLMENT
    public static class EnrollRequest {

        public Long courseId;
    }

    @PostMapping
    public ResponseEntity<Enrollment> enroll(@RequestBody EnrollRequest req, Authentication authentication) {
        if (req.courseId == null) {
            return ResponseEntity.badRequest().build();
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Enrollment> alreadyEnrolled = enrollmentService.getEnrollmentByUser_IdAndCourse_CourseId(
            user.getId(),
            req.courseId
        );
        if (!alreadyEnrolled.isEmpty()) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        log.info("Enrolling user {} in course {}", user.getId(), req.courseId);

        Enrollment enrollment = enrollmentService.enrollUser(user.getId(), req.courseId);

        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkEnrollment(@RequestParam long courseId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Enrollment> alreadyEnrolled = enrollmentService.getEnrollmentByUser_IdAndCourse_CourseId(
            user.getId(),
            courseId
        );
        return ResponseEntity.ok(alreadyEnrolled.isPresent());
    }

    // GET USER ENROLLMENTS
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByUser(userId));
    }

    // COMPLETE COURSE
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.markCompleted(id));
    }

    // DROP COURSE
    @PutMapping("/{id}/drop")
    public ResponseEntity<?> dropEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.markDropped(id));
    }
}
