package com.learnnest.lms.api.admin.dto;

import java.util.UUID;

public record AdminTestOptionResponse(
        UUID id,
        String optionText,
        boolean correct
) {
}
