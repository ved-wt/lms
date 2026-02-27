package com.learn.lms.controller;

import com.learn.lms.model.ContentType;
import com.learn.lms.model.Course;
import com.learn.lms.model.Lesson;
import com.learn.lms.model.Section;
import com.learn.lms.model.User;
import com.learn.lms.service.CourseService;
import com.learn.lms.service.UserService;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    // @GetMapping
    // public List<Course> all() {
    //     return courseService.getAllCourses();
    // }

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) String filter, Authentication auth) {
        System.out.println("FILTER RECEIVED = " + filter);
        if (filter == null) {
            return courseService.getAllCourses();
        }

        String username = auth.getName();
        User user = userService.getUserByUsername(username);

        switch (filter) {
            case "explore":
                return courseService.getExploreCourses(user.getId());
            case "my":
                return courseService.getCoursesByInstructor(user.getId());
            default:
                return courseService.getAllCourses();
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        Course c = courseService.getCourseById(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    public static class CourseCreateRequest {

        public String courseName;
        public String courseDescription;
        public Long instructorId;
        public List<SectionRequest> sections;
    }

    public static class SectionRequest {

        public String title;
        public String description;
        public int orderIndex;
        public List<LessonRequest> lessons;
    }

    public static class LessonRequest {

        public String title;
        public String description;
        public int orderIndex;
        public String content;
        public String contentType;
        public String videoUrl;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody CourseCreateRequest req) {
        Course saved = courseService.createCourse(req);
        return ResponseEntity.created(URI.create("/api/courses/" + saved.getCourseId())).body(saved);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long courseId, Authentication auth) {
        Course course = courseService.getCourseById(courseId);
        String currentUsername = auth.getName();

        if (!course.getInstructor().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this course");
        }
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // Note to self: These will be useful for when instructors want to edit their courses.
    // instead of using the createCourse endpoint, we can simply update only a section/lesson using courseID.
    // Seems genius :)
    public static class SectionCreateRequest {

        public String title;
        public Integer orderIndex;
    }

    @PostMapping("/{courseId}/sections")
    public ResponseEntity<Section> addSection(@PathVariable Long courseId, @RequestBody SectionCreateRequest req) {
        Section s = new Section();
        s.setTitle(req.title);
        s.setOrderIndex(req.orderIndex);
        Section saved = courseService.addSectionToCourse(courseId, s);
        if (saved == null) return ResponseEntity.notFound().build();
        return ResponseEntity.created(URI.create("/api/sections/" + saved.getSectionId())).body(saved);
    }

    public static class LessonCreateRequest {

        public String title;
        public String contentType;
        public String content;
        public Integer orderIndex;
    }

    @PostMapping("/sections/{sectionId}/lessons")
    public ResponseEntity<Lesson> addLesson(@PathVariable Long sectionId, @RequestBody LessonCreateRequest req) {
        Lesson l = new Lesson();
        l.setTitle(req.title);
        if (req.contentType != null) {
            try {
                l.setContentType(ContentType.valueOf(req.contentType.toUpperCase()));
            } catch (Exception e) {}
        }
        l.setContent(req.content);
        l.setOrderIndex(req.orderIndex);
        Lesson saved = courseService.addLessonToSection(sectionId, l);
        if (saved == null) return ResponseEntity.notFound().build();
        return ResponseEntity.created(URI.create("/api/lessons/" + saved.getLessonId())).body(saved);
    }

    @PutMapping("/{courseId:\\d+}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable Long courseId,
        @RequestBody CourseCreateRequest req,
        Authentication auth
    ) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
