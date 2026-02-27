package com.learn.lms.controller;

import com.learn.lms.model.Lesson;
import com.learn.lms.model.User;
import com.learn.lms.service.LessonService;
import com.learn.lms.service.ProgressService;
import com.learn.lms.service.UserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private LessonService lessonService;
    private ProgressService progressService;
    private UserService userService;

    public LessonController(LessonService lessonService, ProgressService progressService, UserService userService) {
        this.lessonService = lessonService;
        this.progressService = progressService;
        this.userService = userService;
    }

    @Data
    public class LessonResponse {

        private Lesson lesson;
        private boolean isCompleted;

        public LessonResponse(Lesson lesson, boolean isCompleted) {
            this.lesson = lesson;
            this.isCompleted = isCompleted;
        }

        public Lesson getLesson() {
            return lesson;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public void setLesson(Lesson lesson) {
            this.lesson = lesson;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable long lessonId, Authentication auth) {
        String username = auth.getName();
        User user = userService.getUserByUsername(username);

        Lesson lesson = lessonService.getLesson(lessonId);
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }
        boolean completed = progressService.isCompleted(user.getId(), lessonId);

        return ResponseEntity.ok(new LessonResponse(lesson, completed));
    }
}
