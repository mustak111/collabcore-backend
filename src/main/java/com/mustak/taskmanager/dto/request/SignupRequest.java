package com.mustak.taskmanager.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}