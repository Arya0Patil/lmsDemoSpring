package com.learnnest.lms.domain.repository;

import com.learnnest.lms.domain.model.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, UUID> {
    List<TestAttempt> findAllByUserIdAndTestIdIn(UUID userId, Collection<UUID> testIds);
}
