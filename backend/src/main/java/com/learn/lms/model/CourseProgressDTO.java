package com.learn.lms.model;

import lombok.Data;

@Data
public class CourseProgressDTO {

    public CourseProgressDTO(
        String courseName2,
        long courseId,
        double progressPercentage,
        long completedLessonsCount,
        long totalLessonsCount
    ) {
        this.courseName = courseName2;
        this.courseId = courseId;
        this.progressPercentage = progressPercentage;
        this.completedLessons = completedLessonsCount;
        this.totalLessons = totalLessonsCount;
    }

    private String courseName;
    private long courseId;
    private double progressPercentage;
    private long completedLessons;
    private long totalLessons;
}
