package com.learnnest.lms.domain.repository.enrollment;

import com.learnnest.lms.domain.model.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    @EntityGraph(attributePaths = {"course", "user"})
    List<Enrollment> findAllByCourseId(UUID courseId);
    @EntityGraph(attributePaths = {"course", "user"})
    List<Enrollment> findAllByUserId(UUID userId);
    Optional<Enrollment> findByCourseIdAndUserId(UUID courseId, UUID userId);
    boolean existsByCourseIdAndUserId(UUID courseId, UUID userId);
}
