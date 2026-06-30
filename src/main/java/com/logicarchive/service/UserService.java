package com.logicarchive.service;

import com.logicarchive.dto.UserRequest;
import com.logicarchive.dto.UserResponse;
import com.logicarchive.entity.User;
import com.logicarchive.exception.ResourceNotFoundException;
import com.logicarchive.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User saved = userRepository.save(user);
        return new UserResponse(saved);
    }

    public UserResponse getUserById(Long id) {
        return new UserResponse(findUser(id));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
