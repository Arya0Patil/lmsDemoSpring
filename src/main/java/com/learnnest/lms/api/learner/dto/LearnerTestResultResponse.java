package com.learnnest.lms.api.learner.dto;

import java.util.List;
import java.util.UUID;

public record LearnerTestResultResponse(
        UUID testId,
        int score,
        int totalQuestions,
        int passingScore,
        boolean passed,
        List<LearnerTestAnswerReview> answers
) {
}
