package com.mustak.taskmanager.dto.response;

import com.mustak.taskmanager.model.Task;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Task.Priority priority;
    private Task.Status status;
    private String assigneeName;
    private Long assigneeId;
    private String createdByName;
    private LocalDateTime createdAt;
}