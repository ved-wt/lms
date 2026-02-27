package com.learn.lms.repository;

import com.learn.lms.model.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor_Id(Long instructorId);
    List<Course> findByInstructor_IdNot(Long instructorId);
}
