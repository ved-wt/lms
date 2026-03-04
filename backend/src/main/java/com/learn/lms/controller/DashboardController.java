package com.learn.lms.controller;

import com.learn.lms.dto.CourseProgressDTO;
import com.learn.lms.model.User;
import com.learn.lms.repository.UserRepository;
import com.learn.lms.service.CourseService;
import com.learn.lms.service.EnrollmentService;
import com.learn.lms.service.ProgressService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CourseService courseService;
    private final ProgressService progressService;
    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    public DashboardController(
        CourseService courseService,
        ProgressService progressService,
        EnrollmentService enrollmentService,
        UserRepository userRepository
    ) {
        this.courseService = courseService;
        this.progressService = progressService;
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/progress")
    public ResponseEntity<List<CourseProgressDTO>> getDashboardProgress(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // List<Enrollment> enrollments = enrollmentService.getEnrollmentsByUser(user.getId());

        // List<CourseProgressDTO> courseProgress = enrollments
        //     .stream()
        //     .map(en -> {
        //         Course c = en.getCourse();
        //         long progress = progressService.getCompletedLessonsCount(user.getId(), c.getCourseId());
        //         long totalLessons = courseService.getTotalLessonsCount(c.getCourseId());
        //         double progressPercentage = (double) (progress / totalLessons) * 100;
        //         return new CourseProgressDTO(
        //             c.getCourseName(),
        //             c.getCourseId(),
        //             progressPercentage,
        //             progress,
        //             totalLessons
        //         );
        //     })
        //     .toList();

        List<CourseProgressDTO> courseProgress = enrollmentService.getProgressForUser(user.getId());

        return ResponseEntity.ok(courseProgress);
    }
}
