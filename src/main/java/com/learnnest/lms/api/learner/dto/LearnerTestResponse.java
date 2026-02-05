package com.learnnest.lms.api.learner.dto;

import java.util.List;
import java.util.UUID;

public record LearnerTestResponse(
        UUID id,
        String title,
        int totalQuestions,
        int passingScore,
        List<LearnerTestQuestionResponse> questions
) {
}
