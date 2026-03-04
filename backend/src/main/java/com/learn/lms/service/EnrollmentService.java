package com.learn.lms.service;

import com.learn.lms.dto.CourseProgressDTO;
import com.learn.lms.model.Course;
import com.learn.lms.model.Enrollment;
import com.learn.lms.model.EnrollmentStatus;
import com.learn.lms.model.User;
import com.learn.lms.repository.CourseRepository;
import com.learn.lms.repository.EnrollmentRepository;
import com.learn.lms.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;
    private UserRepository userRepository;
    private CourseRepository courseRepository;

    public EnrollmentService(
        EnrollmentRepository enrollmentRepository,
        UserRepository userRepository,
        CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public Enrollment enrollUser(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollment.setEnrolledAt(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment markCompleted(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository
            .findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletedAt(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment markDropped(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository
            .findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.DROPPED);

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollmentsByUser(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public Optional<Enrollment> getEnrollmentByUser_IdAndCourse_CourseId(Long userId, Long courseId) {
        return enrollmentRepository.findByUser_IdAndCourse_CourseId(userId, courseId);
    }

    public List<CourseProgressDTO> getProgressForUser(long userId) {
        return enrollmentRepository.getProgressForUser(userId);
    }
}
