package com.learnnest.lms.api.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCourseRequest(
        @NotBlank String title,
        @NotBlank String description
) {
}
