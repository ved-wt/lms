
package com.learn.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lms.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    public List<Enrollment> findByUserId(Long userId);

    public Optional<Enrollment> findByUser_IdAndCourse_CourseId(Long userId, Long courseId);

    public boolean existsByUser_IdAndCourse_CourseId(Long userId, Long courseId);
}
