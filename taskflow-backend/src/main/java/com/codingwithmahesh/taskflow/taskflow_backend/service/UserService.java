package com.codingwithmahesh.taskflow.taskflow_backend.service;

import com.codingwithmahesh.taskflow.taskflow_backend.dto.user.RegisterUser;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.user.UserResponse;
import com.codingwithmahesh.taskflow.taskflow_backend.entity.User;
import com.codingwithmahesh.taskflow.taskflow_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(RegisterUser registerUser) {
        User user = mapToUserEntity(registerUser);
        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }


    private User mapToUserEntity(RegisterUser registerUser) {
        User user = new User();
        user.setFullName(registerUser.getFullName());
        user.setUsername(registerUser.getUsername());
        user.setPassword(registerUser.getPassword());
        user.setEmail(registerUser.getEmail());

        return user;
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
