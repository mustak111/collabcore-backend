package com.mustak.taskmanager.controller;

import com.mustak.taskmanager.dto.request.LoginRequest;
import com.mustak.taskmanager.dto.request.SignupRequest;
import com.mustak.taskmanager.dto.response.AuthResponse;
import com.mustak.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}