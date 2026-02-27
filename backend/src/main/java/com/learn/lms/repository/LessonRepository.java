package com.learn.lms.repository;

import com.learn.lms.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    long countBySection_Course_CourseId(Long courseId);

    Lesson findByLessonId(long lessonId);
}
