package com.pg.auth_service.service;

import com.pg.auth_service.client.UserServiceClient;
import com.pg.auth_service.dto.AuthResponse;
import com.pg.auth_service.dto.LoginRequest;
import com.pg.auth_service.dto.RefreshTokenRequest;
import com.pg.auth_service.dto.RegisterRequest;
import com.pg.auth_service.dto.UserProfileCreateRequest;
import com.pg.auth_service.entity.RefreshToken;
import com.pg.auth_service.entity.Role;
import com.pg.auth_service.entity.UserCredential;
import com.pg.auth_service.exception.InvalidTokenException;
import com.pg.auth_service.exception.UserAlreadyExistsException;
import com.pg.auth_service.repository.RefreshTokenRepository;
import com.pg.auth_service.repository.RoleRepository;
import com.pg.auth_service.repository.UserRepository;
import com.pg.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceClient userServiceClient;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${internal.service-token}")
    private String internalServiceToken;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new UserAlreadyExistsException("Mobile already registered: " + request.getMobile());
        }

        Role role = roleRepository.findByRoleName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("USER").build()));

        UserCredential user = UserCredential.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .role(role)
                .build();

        userRepository.save(user);
        log.info("User registered with id: {}, calling user-service to create profile", user.getId());

        try {
            userServiceClient.createUserProfile(
                    internalServiceToken,
                    UserProfileCreateRequest.builder()
                            .authUserId(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .mobile(user.getMobile())
                            .build()
            );
            log.info("User profile created in user-service for authUserId: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to create user profile in user-service for authUserId: {}. Error: {}",
                    user.getId(), e.getMessage());
            // Registration is not rolled back — profile can be retried
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserCredential user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken existing = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (existing.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(existing);
            throw new InvalidTokenException("Refresh token expired. Please login again.");
        }

        UserCredential user = existing.getUser();
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName(), user.getId());

        return AuthResponse.builder()
                .authUserId(user.getId())
                .accessToken(accessToken)
                .refreshToken(existing.getToken())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .build();
    }

    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(refreshTokenRepository::deleteByUser);
    }

    private AuthResponse buildAuthResponse(UserCredential user) {
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().getRoleName(), user.getId());
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .authUserId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .build();
    }
}
