package com.learnnest.lms.api.learner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record LearnerTestSubmitRequest(
        @NotEmpty List<@Valid LearnerTestAnswerRequest> answers
) {
}
