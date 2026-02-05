package com.learnnest.lms.api.admin.dto;

import java.util.List;
import java.util.UUID;

public record AdminTestDetailResponse(
        UUID id,
        String title,
        int totalQuestions,
        int passingScore,
        List<AdminTestQuestionResponse> questions
) {
}
