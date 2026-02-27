package com.learn.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.lms.model.LessonProgress;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    boolean existsByUser_IdAndLesson_LessonId(Long userId, Long lessonId);

    List<LessonProgress> findByUser_IdAndLesson_Section_Course_CourseId(Long userId, Long courseId);

    long countByUser_IdAndLesson_Section_Course_CourseId(Long userId, Long courseId);

}
