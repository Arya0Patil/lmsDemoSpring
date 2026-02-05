package com.learnnest.lms.domain.service.auth;

import com.learnnest.lms.api.auth.dto.AuthRequest;
import com.learnnest.lms.api.auth.dto.AuthResponse;
import com.learnnest.lms.api.auth.dto.RegisterRequest;
import com.learnnest.lms.api.auth.dto.UserProfileResponse;
import com.learnnest.lms.domain.model.auth.UserAccount;
import com.learnnest.lms.domain.repository.auth.UserAccountRepository;
import com.learnnest.lms.infra.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserAccount user = new UserAccount(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                "ROLE_STUDENT"
        );

        userAccountRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = jwtService.generateToken(authentication.getName());
        return new AuthResponse(token, "Bearer");
    }

    public UserProfileResponse getProfile(String email) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
