package com.mustak.taskmanager.controller;

import com.mustak.taskmanager.dto.request.TaskRequest;
import com.mustak.taskmanager.dto.response.TaskResponse;
import com.mustak.taskmanager.repository.UserRepository;
import com.mustak.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    private Long getCurrentUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"))
                .getId();
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, request, userId));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        return ResponseEntity.ok(
                taskService.getProjectTasks(projectId, userId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        return ResponseEntity.ok(
                taskService.updateTask(taskId, request, userId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getCurrentUserId(userDetails);
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.ok("Task deleted successfully");
    }
}