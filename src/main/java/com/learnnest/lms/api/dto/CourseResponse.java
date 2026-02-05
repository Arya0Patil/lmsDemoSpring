package com.learnnest.lms.api.dto;

import java.util.List;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        String description,
        List<SubjectResponse> subjects
) {
}
