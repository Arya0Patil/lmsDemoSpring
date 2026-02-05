package com.learnnest.lms.domain.repository;

import com.learnnest.lms.domain.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
}
