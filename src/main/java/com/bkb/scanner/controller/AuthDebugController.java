package com.bkb.scanner.controller;

import com.bkb.scanner.entity.User;
import com.bkb.scanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/debug")
@RequiredArgsConstructor
@Slf4j
public class AuthDebugController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/test-db")
    public ResponseEntity<?> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            long userCount = userRepository.count();
            response.put("userCount", userCount);
            response.put("databaseConnected", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("databaseConnected", false);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/check-user/{email}")
    public ResponseEntity<?> checkUser(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("found", true);
                response.put("userId", user.getUserId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("role", user.getRole().name());
                response.put("active", user.isActive());
                response.put("enabled", user.isEnabled());
            } else {
                response.put("found", false);
                response.put("message", "User not found with email: " + email);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-password")
    public ResponseEntity<?> testPassword() {
        Map<String, Object> response = new HashMap<>();
        try {
            String testPassword = "password123";
            String encoded = passwordEncoder.encode(testPassword);
            boolean matches = passwordEncoder.matches(testPassword, encoded);

            response.put("testPassword", testPassword);
            response.put("encodedPassword", encoded);
            response.put("passwordMatches", matches);

            // Check if Jane's password is correct
            var userOpt = userRepository.findByEmail("jane.doe@example.com");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean janePasswordMatches = passwordEncoder.matches(testPassword, user.getPassword());
                response.put("janePasswordMatches", janePasswordMatches);
                response.put("janeEncodedPassword", user.getPassword());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/list-users")
    public ResponseEntity<?> listUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            var users = userRepository.findAll();
            response.put("totalUsers", users.size());
            response.put("users", users.stream().map(u -> {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("userId", u.getUserId());
                userInfo.put("email", u.getEmail());
                userInfo.put("name", u.getName());
                userInfo.put("role", u.getRole().name());
                return userInfo;
            }).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}