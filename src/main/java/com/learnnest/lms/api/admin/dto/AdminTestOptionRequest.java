package com.learnnest.lms.api.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminTestOptionRequest(
        @NotBlank String optionText,
        boolean correct
) {
}
