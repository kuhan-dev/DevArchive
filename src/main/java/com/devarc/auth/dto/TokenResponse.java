package com.devarc.auth.dto;

public record TokenResponse(
        String tokenType,
        String accessToken,
        long accessTokenExpiresIn,
        String refreshToken,
        long refreshTokenExpiresIn
) {
    public static TokenResponse bearer(
            String accessToken,
            long accessTokenExpiresIn,
            String refreshToken,
            long refreshTokenExpiresIn
    ) {
        return new TokenResponse(
                "Bearer",
                accessToken,
                accessTokenExpiresIn,
                refreshToken,
                refreshTokenExpiresIn
        );
    }
}
