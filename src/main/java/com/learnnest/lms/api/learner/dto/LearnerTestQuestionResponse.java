package com.learnnest.lms.api.learner.dto;

import java.util.List;
import java.util.UUID;

public record LearnerTestQuestionResponse(
        UUID id,
        String questionText,
        String difficulty,
        List<LearnerTestOptionResponse> options
) {
}
