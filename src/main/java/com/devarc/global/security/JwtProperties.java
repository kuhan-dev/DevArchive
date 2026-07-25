package com.devarc.global.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @Min(60000) long accessTokenExpiration,
        @Min(60000) long refreshTokenExpiration
) {
}
