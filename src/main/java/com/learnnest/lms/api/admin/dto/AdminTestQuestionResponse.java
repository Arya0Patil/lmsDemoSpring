package com.learnnest.lms.api.admin.dto;

import java.util.List;
import java.util.UUID;

public record AdminTestQuestionResponse(
        UUID id,
        String questionText,
        String difficulty,
        int negativeMark,
        String explanation,
        List<AdminTestOptionResponse> options
) {
}
