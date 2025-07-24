package com.bkb.scanner.service;

import com.bkb.scanner.entity.User;
import com.bkb.scanner.repository.UserRepository;
import com.bkb.scanner.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    public User updateUser(String userId, User userUpdate) {
        User existingUser = getUserByUserId(userId);

        if (userUpdate.getName() != null) {
            existingUser.setName(userUpdate.getName());
        }
        if (userUpdate.getDepartment() != null) {
            existingUser.setDepartment(userUpdate.getDepartment());
        }
        if (userUpdate.getProfilePicture() != null) {
            existingUser.setProfilePicture(userUpdate.getProfilePicture());
        }

        return userRepository.save(existingUser);
    }
}