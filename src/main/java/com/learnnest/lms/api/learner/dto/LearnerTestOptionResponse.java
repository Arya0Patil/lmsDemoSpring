package com.learnnest.lms.api.learner.dto;

import java.util.UUID;

public record LearnerTestOptionResponse(
        UUID id,
        String optionText
) {
}
