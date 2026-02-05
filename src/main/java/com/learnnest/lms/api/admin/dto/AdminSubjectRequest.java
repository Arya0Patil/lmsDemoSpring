package com.learnnest.lms.api.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminSubjectRequest(
        @NotBlank String title,
        @NotBlank String summary,
        String contentBody
) {
}
