package com.learn.lms.service;

import com.learn.lms.model.Course;
import com.learn.lms.model.Enrollment;
import com.learn.lms.model.EnrollmentStatus;
import com.learn.lms.model.Lesson;
import com.learn.lms.model.LessonProgress;
import com.learn.lms.repository.EnrollmentRepository;
import com.learn.lms.repository.LessonProgressRepository;
import com.learn.lms.repository.LessonRepository;
import com.learn.lms.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {

    private final LessonProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public ProgressService(
        LessonProgressRepository progressRepository,
        EnrollmentRepository enrollmentRepository,
        LessonRepository lessonRepository,
        UserRepository userRepository
    ) {
        this.progressRepository = progressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void markAsCompleted(Long userId, Long lessonId) {
        if (progressRepository.existsByUser_IdAndLesson_LessonId(userId, lessonId)) {
            throw new RuntimeException("Lesson already completed");
        }
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        Course course = lesson.getSection().getCourse();

        LessonProgress progress = new LessonProgress();
        progress.setUser(userRepository.findById(userId).orElseThrow());
        progress.setLesson(lesson);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        updateCourseCompletionStatus(userId, course.getCourseId());
    }

    private void updateCourseCompletionStatus(Long userId, Long courseId) {
        long totalLessons = lessonRepository.countBySection_Course_CourseId(courseId);
        long completedLessons = progressRepository.countByUser_IdAndLesson_Section_Course_CourseId(userId, courseId);

        if (totalLessons > 0 && totalLessons == completedLessons) {
            Enrollment enrollment = enrollmentRepository
                .findByUser_IdAndCourse_CourseId(userId, courseId)
                .orElseThrow();
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
        }
    }

    public long getCompletedLessonsCount(Long userId, Long courseId) {
        return progressRepository.countByUser_IdAndLesson_Section_Course_CourseId(userId, courseId);
    }

    public boolean isCompleted(Long userId, Long lessonId) {
        return progressRepository.existsByUser_IdAndLesson_LessonId(userId, lessonId);
    }
}
