package com.learn.lms.model;

import lombok.Data;

@Data
public class CourseProgressDTO {
    public CourseProgressDTO(String courseName2, long progress, long completedLessonsCount, long totalLessonsCount) {
        this.courseName = courseName2;
        this.progressPercentage = (int) progress;
        this.completedLessons = completedLessonsCount;
        this.totalLessons = totalLessonsCount;
    }

    private String courseName;
    private int progressPercentage;
    private long completedLessons;
    private long totalLessons;

}