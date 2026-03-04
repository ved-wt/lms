package com.learn.lms.repository;

import com.learn.lms.dto.CourseProgressDTO;
import com.learn.lms.model.Enrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    public List<Enrollment> findByUserId(Long userId);

    public Optional<Enrollment> findByUser_IdAndCourse_CourseId(Long userId, Long courseId);

    public boolean existsByUser_IdAndCourse_CourseId(Long userId, Long courseId);

    @Query(
        """
        SELECT new com.learn.lms.dto.CourseProgressDTO(
            c.courseId,
            c.courseName,
            COUNT(DISTINCT lp.progressId),
            COUNT(DISTINCT l.lessonId)
        )
        FROM Enrollment e
        JOIN e.course c
        JOIN c.sections s
        JOIN s.lessons l
        LEFT JOIN LessonProgress lp
            ON lp.lesson = l
            AND lp.user = e.user
        WHERE e.user.id = :userId
        GROUP BY c.courseId, c.courseName
        """
    )
    List<CourseProgressDTO> getProgressForUser(@Param("userId") Long userId);
}
