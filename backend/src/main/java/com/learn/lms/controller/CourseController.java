package com.learn.lms.controller;

import com.learn.lms.model.ContentType;
import com.learn.lms.model.Course;
import com.learn.lms.model.Lesson;
import com.learn.lms.model.Section;
import com.learn.lms.model.User;
import com.learn.lms.service.CourseService;
import com.learn.lms.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    // --- DTOs ---

    public static class CourseCreateRequest {

        @NotBlank(message = "Course name cannot be blank")
        public String courseName;

        @Size(min = 10, max = 120, message = "Course description must be 10-120 characters")
        public String courseDescription;

        @NotEmpty(message = "A course must have at least one section")
        @Valid // Validates the objects inside the list
        public List<SectionCreateRequest> sections;
    }

    public static class SectionCreateRequest {

        @NotBlank(message = "Section title cannot be blank")
        public String title;

        @Size(min = 10, max = 120, message = "Section description must be 10-120 characters")
        public String description;

        public int orderIndex;

        @NotEmpty(message = "A section must have at least one lesson")
        @Valid // Validates the objects inside the list
        public List<LessonRequest> lessons;
    }

    public static class LessonRequest {

        @NotBlank(message = "Lesson title cannot be blank")
        public String title;

        public String description;

        @Min(value = 0, message = "Order index must be non-negative")
        public int orderIndex;

        public String content;
        public String contentType;
        public String videoUrl;
    }

    // --- Course Endpoints ---

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) String filter, Authentication auth) {
        if (filter == null) return courseService.getAllCourses();

        String username = auth.getName();
        User user = userService.getUserByUsername(username);

        return switch (filter) {
            case "explore" -> courseService.getExploreCourses(user.getId());
            case "my" -> courseService.getCoursesByInstructor(user.getId());
            default -> courseService.getAllCourses();
        };
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        Course c = courseService.getCourseById(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseCreateRequest req, Authentication auth) {
        Course saved = courseService.createCourse(req, auth.getName());
        return ResponseEntity.created(URI.create("/api/courses/" + saved.getCourseId())).body(saved);
    }

    @PutMapping("/{courseId:\\d+}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable Long courseId,
        @Valid @RequestBody CourseCreateRequest req,
        Authentication auth
    ) {
        Course existing = courseService.getCourseById(courseId);
        if (existing == null) return ResponseEntity.notFound().build();

        if (!existing.getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existing.setCourseName(req.courseName);
        existing.setCourseDescription(req.courseDescription);

        return ResponseEntity.ok(courseService.updateCourse(existing));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, Authentication auth) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) return ResponseEntity.notFound().build();

        if (!course.getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // --- Section Endpoints ---

    @PostMapping("/{courseId}/sections")
    public ResponseEntity<Section> addSection(
        @PathVariable Long courseId,
        @Valid @RequestBody SectionCreateRequest req,
        Authentication auth
    ) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) return ResponseEntity.notFound().build();

        if (!course.getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Section s = new Section();
        s.setTitle(req.title);
        s.setDescription(req.description);
        s.setOrderIndex(req.orderIndex);

        if (req.lessons != null && !req.lessons.isEmpty()) {
            List<Lesson> lessons = req.lessons
                .stream()
                .map(lReq -> {
                    Lesson l = new Lesson();
                    l.setTitle(lReq.title);
                    l.setOrderIndex(lReq.orderIndex);
                    l.setContentType(com.learn.lms.model.ContentType.TEXT);
                    l.setSection(s);
                    return l;
                })
                .toList();
            s.setLessons(lessons);
        }
        // ------------------------------------------------

        Section saved = courseService.addSectionToCourse(courseId, s);
        return ResponseEntity.created(URI.create("/api/sections/" + saved.getSectionId())).body(saved);
    }

    @PutMapping("/sections/{sectionId}")
    public ResponseEntity<Section> updateSection(
        @PathVariable Long sectionId,
        @Valid @RequestBody SectionCreateRequest req,
        Authentication auth
    ) {
        Section s = courseService.getSectionById(sectionId);
        if (s == null) return ResponseEntity.notFound().build();

        if (!s.getCourse().getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        s.setTitle(req.title);
        // s.setDescription(req.description); // Syncing description
        s.setOrderIndex(req.orderIndex);
        return ResponseEntity.ok(courseService.saveSection(s));
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
        @PathVariable Long courseId,
        @PathVariable Long sectionId,
        Authentication auth
    ) {
        Course course = courseService.getCourseById(courseId);
        if (course == null || !course.getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        courseService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    // --- Lesson Endpoints ---

    @PostMapping("/sections/{sectionId}/lessons")
    public ResponseEntity<Lesson> addLesson(
        @PathVariable Long sectionId,
        @Valid @RequestBody LessonRequest req,
        Authentication auth
    ) {
        Section section = courseService.getSectionById(sectionId);
        if (section == null) return ResponseEntity.notFound().build();

        // Ownership Check via Course
        if (!section.getCourse().getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Lesson l = new Lesson();
        l.setTitle(req.title);
        // l.setDescription(req.description); // Syncing description
        l.setContent(req.content);
        l.setOrderIndex(req.orderIndex);
        l.setVideoUrl(req.videoUrl);

        if (req.contentType != null) {
            try {
                l.setContentType(ContentType.valueOf(req.contentType.toUpperCase()));
            } catch (Exception e) {
                l.setContentType(ContentType.TEXT);
            }
        }

        Lesson saved = courseService.addLessonToSection(sectionId, l);
        return ResponseEntity.created(URI.create("/api/lessons/" + saved.getLessonId())).body(saved);
    }

    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(
        @PathVariable Long lessonId,
        @Valid @RequestBody LessonRequest req,
        Authentication auth
    ) {
        Lesson l = courseService.getLessonById(lessonId);
        if (l == null) return ResponseEntity.notFound().build();

        if (!l.getSection().getCourse().getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        l.setTitle(req.title);
        // l.setDescription(req.description); // Syncing description
        l.setContent(req.content);
        l.setOrderIndex(req.orderIndex);
        l.setVideoUrl(req.videoUrl);

        if (req.contentType != null) {
            try {
                l.setContentType(ContentType.valueOf(req.contentType.toUpperCase()));
            } catch (Exception e) {
                l.setContentType(ContentType.TEXT);
            }
        }

        return ResponseEntity.ok(courseService.saveLesson(l));
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
        @PathVariable Long courseId,
        @PathVariable Long sectionId,
        @PathVariable Long lessonId,
        Authentication auth
    ) {
        Course course = courseService.getCourseById(courseId);
        if (course == null || !course.getInstructor().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        courseService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
