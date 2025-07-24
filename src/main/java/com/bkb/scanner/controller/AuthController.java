package com.bkb.scanner.controller;

import com.bkb.scanner.entity.User;
import com.bkb.scanner.security.JwtUtil;
import com.bkb.scanner.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")  // Changed from /api/auth to just /auth since context-path adds /api
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth endpoint is working");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for email: {}", loginRequest.getEmail());

            // Check if user exists
            var userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                log.error("User not found with email: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            log.info("User found, attempting authentication...");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            log.info("Authentication successful");

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the authenticated user
            User user = userOpt.get();
            String jwt = jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");

            // Get user details
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getUserId());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole().name());

            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            error.put("message", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();

            if (jwtUtil.validateToken(refreshToken)) {
                String userEmail = jwtUtil.getUserEmailFromToken(refreshToken);
                var userOpt = userRepository.findByEmail(userEmail);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    String newAccessToken = jwtUtil.generateToken(user);

                    Map<String, Object> response = new HashMap<>();
                    response.put("accessToken", newAccessToken);
                    response.put("tokenType", "Bearer");

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        } catch (Exception e) {
            log.error("Token refresh failed: ", e);
            return ResponseEntity.status(401).body(Map.of("error", "Token refresh failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Since we're using stateless JWT, we just return success
        // The client should remove the token from their storage
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getUserId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().name());

        return ResponseEntity.ok(userInfo);
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}