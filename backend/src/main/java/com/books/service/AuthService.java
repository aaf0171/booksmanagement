package com.books.service;

import com.books.dto.ActivationResponseDTO;
import com.books.dto.LoginRequestDTO;
import com.books.dto.LoginResponseDTO;
import com.books.dto.RefreshRequestDTO;
import com.books.dto.RefreshResponseDTO;
import com.books.enums.ActivationStatus;
import com.books.exception.ActivationTokenException;
import com.books.exception.LoginNotFoundException;
import com.books.exception.PasswordMismatchException;
import com.books.model.ActivationToken;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
import com.books.service.ActivationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginsRepository loginsRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenService refreshTokenService;
    private final ActivationTokenService activationTokenService;

    private long accessTokenExpirySeconds;

    @Value("${auth.jwt.access-token-expiry-seconds:900}")
    public void setAccessTokenExpirySeconds(long accessTokenExpirySeconds) {
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
    }

    public void setAccessTokenExpirySecondsForTest(long accessTokenExpirySeconds) {
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Login login = loginsRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new LoginNotFoundException(request.getUsername()));

        if (!login.getEnabled()) {
            throw new LoginNotFoundException("Invalid credentials or account not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), login.getPasswordHash())) {
            throw new LoginNotFoundException("Invalid credentials or account not active");
        }

        List<String> roles = login.getRoles().stream()
                .map(com.books.model.Role::getName)
                .toList();

        login.setLastLogin(java.time.LocalDateTime.now());
        loginsRepository.save(login);

        String accessToken = jwtTokenGenerator.generate(login.getId(), login.getUsername(), roles);
        String refreshToken = refreshTokenService.create(login.getId());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpirySeconds)
                .build();
    }

    public RefreshResponseDTO refresh(String refreshToken) {
        if (!refreshTokenService.isValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token is expired, revoked, or invalid");
        }

        var tokenEntity = refreshTokenService.findValidToken(refreshToken).orElseThrow();
        Long loginId = tokenEntity.getLoginId();

        Login login = loginsRepository.findById(loginId)
                .orElseThrow(() -> new LoginNotFoundException("Login not found"));

        List<String> roles = login.getRoles().stream()
                .map(com.books.model.Role::getName)
                .toList();

        refreshTokenService.revoke(refreshToken);

        String newAccessToken = jwtTokenGenerator.generate(login.getId(), login.getUsername(), roles);

        return RefreshResponseDTO.builder()
                .accessToken(newAccessToken)
                .expiresIn(accessTokenExpirySeconds)
                .build();
    }

    public void logout(String refreshToken) {
        if (!refreshTokenService.isValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token is expired, revoked, or invalid");
        }

        refreshTokenService.revoke(refreshToken);
    }

    public ActivationResponseDTO activate(String token, String password, String confirmPassword) {
        ActivationResponseDTO result = activationTokenService.findTokenResult(token);

        ActivationStatus status = result.status();
        if (status == ActivationStatus.TOKEN_EXPIRED || status == ActivationStatus.TOKEN_INVALID || 
            status == ActivationStatus.ALREADY_ACTIVATED) {
            return result;
        }

        if (status != ActivationStatus.SUCCESS) {
            return new ActivationResponseDTO(ActivationStatus.TOKEN_INVALID, null, null);
        }

        ActivationToken activationToken = activationTokenService
                                .findValidToken(token)
                                .orElseThrow(() -> new ActivationTokenException("Invalid or expired activation token"));
        Login login = loginsRepository
                        .findById(activationToken.getLoginId())
                        .orElseThrow(() -> new ActivationTokenException("Invalid or expired activation token"));

        if (password != null && confirmPassword != null) {
            if (!password.equals(confirmPassword)) {
                throw new PasswordMismatchException("Passwords do not match");
            }

            String hashedPassword = passwordEncoder.encode(password);
            login.setPasswordHash(hashedPassword);
        }

        login.setEnabled(true);
        loginsRepository.save(login);
        activationTokenService.markTokenAsUsed(activationToken.getId());

        return new ActivationResponseDTO(ActivationStatus.SUCCESS, "Account activated successfully", null);
    }
}
