package com.learnnest.lms.api.learner.dto;

import java.util.List;
import java.util.UUID;

public record LearnerSubjectJourneyResponse(
        UUID id,
        String title,
        String summary,
        String contentBody,
        List<LearnerTestJourneyResponse> tests
) {
}
