package com.learn.lms.controller;

import com.learn.lms.model.Course;
import com.learn.lms.model.User;
import com.learn.lms.repository.UserRepository;
import com.learn.lms.service.CourseService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructor/courses")
public class InstructorCourseController {

    private final CourseService courseService;
    private final UserRepository userRepository;

    public InstructorCourseController(CourseService courseService, UserRepository userRepository) {
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<List<Course>> getMyCourses(Authentication auth) {
        String username = auth.getName();
        User instructor = userRepository.findByUsername(username).orElseThrow();

        List<Course> myCourses = courseService.getCoursesByInstructor(instructor.getId());
        return ResponseEntity.ok(myCourses);
    }
}
