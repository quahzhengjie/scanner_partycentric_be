package com.bkb.scanner.controller;

import com.bkb.scanner.dto.ApiResponse;
import com.bkb.scanner.entity.User;
import com.bkb.scanner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
// Option 2: If you must keep @CrossOrigin, use specific origins:

public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserByUserId(userId));
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable String userId,
            @RequestBody User userUpdate,
            @AuthenticationPrincipal User currentUser) {

        if (!currentUser.getUserId().equals(userId) &&
                currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userService.updateUser(userId, userUpdate));
    }
}