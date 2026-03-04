package com.learn.lms.repository;

import com.learn.lms.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Override
    @EntityGraph(attributePaths = { "instructor", "sections", "sections.lessons" })
    Optional<Course> findById(Long id);

    // @Query("SELEct c FROM Course c JOIN FETCH c.instructor JOIN FETCH c.sections ")
    @EntityGraph(attributePaths = { "instructor", "sections" })
    List<Course> findByInstructor_Id(Long instructorId);

    @EntityGraph(attributePaths = { "instructor", "sections" })
    List<Course> findByInstructor_IdNot(Long instructorId);
}
