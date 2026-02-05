package com.learnnest.lms.api.dto;

import java.util.UUID;

public record SubjectTestResponse(
        UUID id,
        String title,
        int totalQuestions,
        int passingScore
) {
}
