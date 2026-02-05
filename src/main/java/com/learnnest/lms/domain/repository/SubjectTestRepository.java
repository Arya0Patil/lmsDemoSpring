package com.learnnest.lms.domain.repository;

import com.learnnest.lms.domain.model.SubjectTest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubjectTestRepository extends JpaRepository<SubjectTest, UUID> {
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<SubjectTest> findWithQuestionsById(UUID id);
}
