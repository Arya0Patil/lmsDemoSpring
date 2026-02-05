package com.learnnest.lms.api.learner.dto;

import java.util.UUID;

public record LearnerTestJourneyResponse(
        UUID id,
        String title,
        int totalQuestions,
        int passingScore,
        boolean attempted,
        boolean passed,
        Integer lastScore
) {
}
