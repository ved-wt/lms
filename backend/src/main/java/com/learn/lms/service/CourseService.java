package com.learn.lms.service;

import com.learn.lms.controller.CourseController.CourseCreateRequest;
import com.learn.lms.model.ContentType;
import com.learn.lms.model.Course;
import com.learn.lms.model.Lesson;
import com.learn.lms.model.Section;
import com.learn.lms.model.User;
import com.learn.lms.repository.CourseRepository;
import com.learn.lms.repository.LessonProgressRepository;
import com.learn.lms.repository.LessonRepository;
import com.learn.lms.repository.SectionRepository;
import com.learn.lms.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;

    public CourseService(
        CourseRepository courseRepository,
        SectionRepository sectionRepository,
        LessonRepository lessonRepository,
        LessonProgressRepository lessonProgressRepository,
        UserRepository userRepository
    ) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.userRepository = userRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getExploreCourses(Long userId) {
        return courseRepository.findByInstructor_IdNot(userId);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructor_Id(instructorId);
    }

    public Course createCourse(CourseCreateRequest req) {
        Course course = new Course();
        course.setCourseName(req.courseName);
        course.setCourseDescription(req.courseDescription);
        // --- Dangerous
        // User instructor = userRepository.findById(req.instructorId).orElse(null);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User instructor = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        course.setInstructor(instructor);

        if (req.sections != null) {
            List<Section> sections = req.sections
                .stream()
                .map(sReq -> {
                    Section section = new Section();
                    section.setTitle(sReq.title);
                    section.setOrderIndex(sReq.orderIndex);
                    section.setCourse(course);

                    if (sReq.lessons != null) {
                        List<Lesson> lessons = sReq.lessons
                            .stream()
                            .map(lReq -> {
                                Lesson lesson = new Lesson();
                                lesson.setTitle(lReq.title);
                                lesson.setContentType(ContentType.valueOf(lReq.contentType));
                                lesson.setContent(lReq.content);
                                lesson.setVideoUrl(lReq.videoUrl);
                                lesson.setOrderIndex(lReq.orderIndex);
                                lesson.setSection(section);
                                return lesson;
                            })
                            .collect(Collectors.toList());
                        section.setLessons(lessons);
                    }
                    return section;
                })
                .collect(Collectors.toList());
            course.setSections(sections);
        }
        return courseRepository.save(course);
    }

    public long getCourseProgress(Long userId, Long courseId) {
        long totalLessons = lessonRepository.countBySection_Course_CourseId(courseId);
        if (totalLessons == 0) return 0;
        long completedLessons = lessonProgressRepository.countByUser_IdAndLesson_Section_Course_CourseId(
            userId,
            courseId
        );
        return ((completedLessons * 100) / totalLessons);
    }

    public long getTotalLessonsCount(Long courseId) {
        return lessonRepository.countBySection_Course_CourseId(courseId);
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    public void deleteSection(Long sectionId) {
        sectionRepository.deleteById(sectionId);
    }

    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id).orElse(null);
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id).orElse(null);
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public Lesson saveLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public Section addSectionToCourse(Long courseId, Section section) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return null;
        section.setCourse(course);
        course.getSections().add(section);
        return sectionRepository.save(section);
    }

    public Lesson addLessonToSection(Long sectionId, Lesson lesson) {
        Section section = sectionRepository.findById(sectionId).orElse(null);
        if (section == null) return null;
        lesson.setSection(section);
        section.getLessons().add(lesson);
        return lessonRepository.save(lesson);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course createCourse(CourseCreateRequest req, String username) {
        User instructor = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = new Course();
        course.setCourseName(req.courseName);
        course.setCourseDescription(req.courseDescription);
        course.setInstructor(instructor);

        if (req.sections != null) {
            List<Section> sections = req.sections
                .stream()
                .map(sReq -> {
                    Section section = new Section();
                    section.setTitle(sReq.title);
                    section.setOrderIndex(sReq.orderIndex);
                    section.setCourse(course);

                    if (sReq.lessons != null) {
                        List<Lesson> lessons = sReq.lessons
                            .stream()
                            .map(lReq -> {
                                Lesson lesson = new Lesson();
                                lesson.setTitle(lReq.title);
                                lesson.setContentType(ContentType.valueOf(lReq.contentType));
                                lesson.setContent(lReq.content);
                                lesson.setVideoUrl(lReq.videoUrl);
                                lesson.setOrderIndex(lReq.orderIndex);
                                lesson.setSection(section);
                                return lesson;
                            })
                            .collect(Collectors.toList());
                        section.setLessons(lessons);
                    }
                    return section;
                })
                .collect(Collectors.toList());
            course.setSections(sections);
        }

        return courseRepository.save(course);
    }
}
