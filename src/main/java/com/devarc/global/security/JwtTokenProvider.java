package com.devarc.global.security;

import com.devarc.global.exception.BusinessException;
import com.devarc.global.exception.ErrorCode;
import com.devarc.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        byte[] secretBytes = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 32바이트 이상이어야 합니다.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String createAccessToken(User user) {
        return createToken(user, properties.accessTokenExpiration(), "access");
    }

    public String createRefreshToken(User user) {
        return createToken(user, properties.refreshTokenExpiration(), "refresh");
    }

    public String getUsernameFromAccessToken(String token) {
        Claims claims = parseClaims(token);
        if (!"access".equals(claims.get("type", String.class))) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        return claims.getSubject();
    }

    public long accessTokenExpiration() {
        return properties.accessTokenExpiration();
    }

    public long refreshTokenExpiration() {
        return properties.refreshTokenExpiration();
    }

    private String createToken(User user, long expirationMillis, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException exception) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }
}
