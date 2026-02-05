package com.learnnest.lms.api.learner.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LearnerTestAnswerRequest(
        @NotNull UUID questionId,
        UUID selectedOptionId
) {
}
