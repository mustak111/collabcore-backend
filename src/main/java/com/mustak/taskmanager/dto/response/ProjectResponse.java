package com.mustak.taskmanager.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String adminName;
    private String role;
    private LocalDateTime createdAt;
}