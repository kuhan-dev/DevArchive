package com.devarc.global.config;

import com.devarc.user.domain.User;
import com.devarc.user.domain.UserRole;
import com.devarc.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitialAdminConfig implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final String email;

    public InitialAdminConfig(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.initial-admin.username}") String username,
            @Value("${app.initial-admin.password}") String password,
            @Value("${app.initial-admin.email}") String email
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        userRepository.save(new User(
                username,
                email.toLowerCase(),
                passwordEncoder.encode(password),
                UserRole.ADMIN
        ));
    }
}
