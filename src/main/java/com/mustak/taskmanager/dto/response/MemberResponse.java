package com.mustak.taskmanager.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;
}