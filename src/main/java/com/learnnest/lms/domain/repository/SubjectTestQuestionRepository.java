package com.learnnest.lms.domain.repository;

import com.learnnest.lms.domain.model.SubjectTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubjectTestQuestionRepository extends JpaRepository<SubjectTestQuestion, UUID> {
}
