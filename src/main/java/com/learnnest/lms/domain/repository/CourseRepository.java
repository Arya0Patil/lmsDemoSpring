package com.learnnest.lms.domain.repository;

import com.learnnest.lms.domain.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    @EntityGraph(attributePaths = {"subjects", "subjects.tests"})
    List<Course> findAll();

    @EntityGraph(attributePaths = {"subjects", "subjects.tests"})
    java.util.Optional<Course> findById(UUID id);
}
