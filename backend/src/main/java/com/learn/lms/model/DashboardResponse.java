package com.learn.lms.model;

public class DashboardResponse {

    public DashboardResponse(java.util.List<CourseProgressDTO> courseProgress) {
        this.courseProgress = courseProgress;
    }

    private java.util.List<CourseProgressDTO> courseProgress;

    public java.util.List<CourseProgressDTO> getCourseProgress() {
        return courseProgress;
    }

    public void setCourseProgress(java.util.List<CourseProgressDTO> courseProgress) {
        this.courseProgress = courseProgress;
    }

}