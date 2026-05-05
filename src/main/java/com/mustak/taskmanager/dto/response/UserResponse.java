package com.mustak.taskmanager.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
}