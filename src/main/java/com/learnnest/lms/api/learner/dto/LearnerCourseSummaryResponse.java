package com.learnnest.lms.api.learner.dto;

import java.util.UUID;

public record LearnerCourseSummaryResponse(
        UUID id,
        String title,
        String description,
        int totalTests,
        int completedTests,
        int progressPercent
) {
}
