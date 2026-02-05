package com.learnnest.lms.api.learner.dto;

import java.util.UUID;

public record LearnerTestAnswerReview(
        UUID questionId,
        UUID selectedOptionId,
        UUID correctOptionId,
        boolean correct,
        String explanation
) {
}
