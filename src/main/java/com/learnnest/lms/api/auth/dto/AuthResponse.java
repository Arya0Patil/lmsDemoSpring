package com.learnnest.lms.api.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType
) {
}
