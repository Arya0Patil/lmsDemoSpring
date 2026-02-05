package com.learnnest.lms.infra.config;

import com.learnnest.lms.domain.model.auth.UserAccount;
import com.learnnest.lms.domain.repository.auth.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeedRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminSeedRunner.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String fullName;
    private final String email;
    private final String password;

    public AdminSeedRunner(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.seed.enabled:false}") boolean enabled,
            @Value("${admin.seed.full-name:Admin User}") String fullName,
            @Value("${admin.seed.email:admin@learnnest.local}") String email,
            @Value("${admin.seed.password:admin12345}") String password
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        if (userAccountRepository.existsByEmail(email)) {
            return;
        }

        UserAccount admin = new UserAccount(
                fullName,
                email,
                passwordEncoder.encode(password),
                "ROLE_ADMIN"
        );

        userAccountRepository.save(admin);
        log.info("Seeded admin user {}", email);
    }
}
