package com.learnnest.lms.api.dto;

import java.util.List;
import java.util.UUID;

public record SubjectResponse(
        UUID id,
        String title,
        String summary,
        String contentBody,
        List<SubjectTestResponse> tests
) {
}
