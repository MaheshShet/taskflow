package com.codingwithmahesh.taskflow.taskflow_backend.service;

import com.codingwithmahesh.taskflow.taskflow_backend.dto.user.AuthResponse;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.user.LoginRequest;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.user.RegisterUser;
import com.codingwithmahesh.taskflow.taskflow_backend.dto.user.UserResponse;
import com.codingwithmahesh.taskflow.taskflow_backend.entity.User;
import com.codingwithmahesh.taskflow.taskflow_backend.exception.BadRequestException;
import com.codingwithmahesh.taskflow.taskflow_backend.exception.DuplicateResourceException;
import com.codingwithmahesh.taskflow.taskflow_backend.repository.UserRepository;
import com.codingwithmahesh.taskflow.taskflow_backend.security.AuthenticatedUser;
import com.codingwithmahesh.taskflow.taskflow_backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse createUser(RegisterUser registerUser) {
        if (!registerUser.getPassword().equals(registerUser.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password must match");
        }

        String username = normalizeText(registerUser.getUsername());
        String email = normalizeEmail(registerUser.getEmail());

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username is already in use");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email is already in use");
        }

        User user = mapToUserEntity(registerUser);
        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        try{
            Authentication authentication =  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(),
                            request.getPassword())
            );

            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            if(authenticatedUser != null) {
                String token = jwtService.generateToken(authenticatedUser);

                AuthResponse authResponse = new AuthResponse();
                authResponse.setToken(token);
                authResponse.setTokenType("Bearer");
                authResponse.setExpiresIn(jwtService.getExpirationMs());
                authResponse.setUser(mapAuthenticatedUserToUserResponse(authenticatedUser));

                return authResponse;
            }
            else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User mapToUserEntity(RegisterUser registerUser) {
        User user = new User();
        user.setFullName(registerUser.getFullName());
        user.setUsername(registerUser.getUsername());
        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
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

    private UserResponse mapAuthenticatedUserToUserResponse(AuthenticatedUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return value.trim();
    }

    private String normalizeEmail(String email) {
        String normalizedEmail = normalizeText(email);
        return normalizedEmail == null ? null : normalizedEmail.toLowerCase(Locale.ROOT);
    }
}
