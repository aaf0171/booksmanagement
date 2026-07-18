package com.books.controller;

import com.books.dto.*;
import com.books.enums.ActivationStatus;
import com.books.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API - login, refresh token, logout")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login with username and password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Access token refreshed",
            content = @Content(schema = @Schema(implementation = RefreshResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        RefreshResponseDTO response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout - revoke refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Token revoked"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get current authenticated user info")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User info retrieved",
            content = @Content(schema = @Schema(implementation = MeResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = auth.getName();
        List<String> authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        MeResponseDTO response = MeResponseDTO.builder()
                .sub(username)
                .login(username)
                .roles(authorities)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate a borrower account using the activation token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account activated successfully",
            content = @Content(schema = @Schema(implementation = ActivationResponseDTO.class))),
        @ApiResponse(responseCode = "200", description = "Account already activated",
            content = @Content(schema = @Schema(implementation = ActivationResponseDTO.class))),
        @ApiResponse(responseCode = "410", description = "Activation token expired",
            content = @Content(schema = @Schema(implementation = ActivationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid activation token",
            content = @Content(schema = @Schema(implementation = ActivationResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Passwords do not match",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/activate")
    public ResponseEntity<ActivationResponseDTO> activate(@Valid @RequestBody ActivationRequestDTO request) {
        ActivationResponseDTO response = authService.activate(request.getToken(), request.getPassword(), request.getConfirmPassword());
        int status = mapStatusToHttpStatus(response.status());
        return ResponseEntity.status(status).body(response);
    }

    private int mapStatusToHttpStatus(ActivationStatus status) {
        return switch (status) {
            case SUCCESS -> 200;
            case ALREADY_ACTIVATED -> 200;
            case TOKEN_EXPIRED -> 410;
            case TOKEN_INVALID -> 400;
        };
    }
}
