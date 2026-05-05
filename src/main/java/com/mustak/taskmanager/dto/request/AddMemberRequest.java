package com.mustak.taskmanager.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddMemberRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;
}