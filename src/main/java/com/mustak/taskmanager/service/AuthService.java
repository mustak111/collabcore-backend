package com.mustak.taskmanager.service;

import com.mustak.taskmanager.dto.request.LoginRequest;
import com.mustak.taskmanager.dto.request.SignupRequest;
import com.mustak.taskmanager.dto.response.AuthResponse;
import com.mustak.taskmanager.exception.ResourceNotFoundException;
import com.mustak.taskmanager.model.User;
import com.mustak.taskmanager.repository.UserRepository;
import com.mustak.taskmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}