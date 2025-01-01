package com.dlskawo0409.demo.auth.dto.response;

import lombok.Builder;

@Builder
public record AccessAndRefreshToken(
        String accessToken,
        String refreshToken
) {
}
