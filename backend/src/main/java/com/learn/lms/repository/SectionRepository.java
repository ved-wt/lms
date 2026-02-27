package com.learn.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lms.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {

}
