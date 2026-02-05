package com.learnnest.lms.api.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AdminSubjectTestRequest(
        @NotBlank String title,
        @Min(1) int passingScore
) {
}
