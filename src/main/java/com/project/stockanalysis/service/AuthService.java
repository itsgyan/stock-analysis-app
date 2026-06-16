package com.project.stockanalysis.service;

import com.project.stockanalysis.dto.AuthResponse;
import com.project.stockanalysis.dto.LoginRequest;
import com.project.stockanalysis.dto.RegisterRequest;
import com.project.stockanalysis.entity.User;
import com.project.stockanalysis.repository.UserRepository;
import com.project.stockanalysis.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling user registration and login authentication logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ─── Register ────────────────────────────────────────────────────────────

    /**
     * Register a new user and return a JWT token on success.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email '" + request.getEmail() + "' is already registered");
        }

        // Build and save the new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        log.info("New user registered: {} ({})", user.getUsername(), user.getEmail());

        // Generate JWT and return
        String token = jwtUtil.generateToken(user);
        return AuthResponse.success(token, user.getUsername(), user.getEmail(),
                user.getRole().name());
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    /**
     * Authenticate an existing user and return a JWT token on success.
     */
    public AuthResponse login(LoginRequest request) {
        // Delegates to Spring Security's DaoAuthenticationProvider
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // Load user from DB (authentication passed, user exists)
        User user = userRepository
                .findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in: {}", user.getUsername());

        String token = jwtUtil.generateToken(user);
        return AuthResponse.success(token, user.getUsername(), user.getEmail(),
                user.getRole().name());
    }
}
