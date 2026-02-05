package com.learnnest.lms.api.learner.dto;

import java.util.List;
import java.util.UUID;

public record LearnerCourseJourneyResponse(
        UUID id,
        String title,
        String description,
        int totalTests,
        int completedTests,
        int progressPercent,
        UUID nextTestId,
        List<LearnerSubjectJourneyResponse> subjects
) {
}
