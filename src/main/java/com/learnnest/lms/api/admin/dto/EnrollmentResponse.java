package com.learnnest.lms.api.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        UUID learnerId,
        String learnerName,
        String learnerEmail,
        Instant enrolledAt
) {
}
