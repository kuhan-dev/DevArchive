package com.devarc.auth.service;

import com.devarc.auth.domain.RefreshToken;
import com.devarc.auth.dto.LoginRequest;
import com.devarc.auth.dto.SignUpRequest;
import com.devarc.auth.dto.TokenResponse;
import com.devarc.auth.dto.UserResponse;
import com.devarc.auth.repository.RefreshTokenRepository;
import com.devarc.global.exception.BusinessException;
import com.devarc.global.exception.ErrorCode;
import com.devarc.global.security.JwtTokenProvider;
import com.devarc.user.domain.User;
import com.devarc.user.domain.UserRole;
import com.devarc.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = new User(
                request.username(),
                request.email().toLowerCase(),
                passwordEncoder.encode(request.password()),
                UserRole.USER
        );
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PASSWORD));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = tokenProvider.createAccessToken(user);
        String refreshToken = tokenProvider.createRefreshToken(user);
        refreshTokenRepository.save(new RefreshToken(
                user,
                sha256(refreshToken),
                LocalDateTime.now().plusNanos(tokenProvider.refreshTokenExpiration() * 1_000_000)
        ));
        return TokenResponse.bearer(
                accessToken,
                tokenProvider.accessTokenExpiration(),
                refreshToken,
                tokenProvider.refreshTokenExpiration()
        );
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", exception);
        }
    }
}
