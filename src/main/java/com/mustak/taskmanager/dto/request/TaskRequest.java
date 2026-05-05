package com.mustak.taskmanager.dto.request;

import com.mustak.taskmanager.model.Task;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private LocalDate dueDate;
    private Task.Priority priority;
    private Task.Status status;
    private Long assignedTo;
}