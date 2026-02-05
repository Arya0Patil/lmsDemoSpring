package com.learnnest.lms.api.admin.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnrollmentRequest(
        @NotNull UUID courseId,
        @NotNull UUID userId
) {
}
