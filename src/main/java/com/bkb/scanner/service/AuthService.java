package com.bkb.scanner.service;

import com.bkb.scanner.entity.User;
import com.bkb.scanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public void updateLastLogin(String userId) {
        userRepository.findByUserId(userId).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            log.info("Updated last login for user: {}", userId);
        });
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<DemoUserDto> getDemoUsers() {
        return userRepository.findAll().stream()
                .map(user -> new DemoUserDto(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name(),
                        "password123" // For demo purposes only
                ))
                .collect(Collectors.toList());
    }

    // DTO for demo users
    public static class DemoUserDto {
        private String userId;
        private String name;
        private String email;
        private String role;
        private String password;

        public DemoUserDto(String userId, String name, String email, String role, String password) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.password = password;
        }

        // Getters
        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getPassword() { return password; }
    }
}