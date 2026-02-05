package com.learnnest.lms.api.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record LearnerResponse(
        UUID id,
        String fullName,
        String email,
        String role,
        Instant createdAt
) {
}
