package com.learn.lms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.lms.model.CourseProgressDTO;
import com.learn.lms.model.DashboardResponse;
import com.learn.lms.service.CourseService;
import com.learn.lms.service.ProgressService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CourseService courseService;
    private final ProgressService progressService;

    public DashboardController(CourseService courseService, ProgressService progressService) {
        this.courseService = courseService;
        this.progressService = progressService;
    }

    @GetMapping("/progress/{userId}")
    public DashboardResponse getDashboardProgress(@PathVariable Long userId) {
        return new DashboardResponse(courseService.getAllCourses().stream().map(c -> {
            long progress = courseService.getCourseProgress(userId, c.getCourseId());
            return new CourseProgressDTO(c.getCourseName(), progress,
                    progressService.getCompletedLessonsCount(userId, c.getCourseId()),
                    courseService.getTotalLessonsCount(c.getCourseId()));
        }).toList());
    }

}
