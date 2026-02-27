package com.learn.lms.service;

import com.learn.lms.model.Lesson;
import com.learn.lms.repository.LessonRepository;
import org.springframework.stereotype.Service;

@Service
public class LessonService {

    private LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Lesson getLesson(long lessonId) {
        return lessonRepository.findByLessonId(lessonId);
    }
}
