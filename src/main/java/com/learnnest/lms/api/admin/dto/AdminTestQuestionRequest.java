package com.learnnest.lms.api.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdminTestQuestionRequest(
        @NotBlank String questionText,
        String difficulty,
        Integer negativeMark,
        String explanation,
        @NotEmpty List<@Valid AdminTestOptionRequest> options
) {
}
