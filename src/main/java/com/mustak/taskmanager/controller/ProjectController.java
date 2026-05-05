package com.mustak.taskmanager.controller;

import com.mustak.taskmanager.dto.request.AddMemberRequest;
import com.mustak.taskmanager.dto.request.ProjectRequest;
import com.mustak.taskmanager.dto.response.MemberResponse;
import com.mustak.taskmanager.dto.response.ProjectResponse;
import com.mustak.taskmanager.dto.response.UserResponse;
import com.mustak.taskmanager.repository.UserRepository;
import com.mustak.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService  projectService;
    private final UserRepository  userRepository;

    private Long getCurrentUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(
                        request, getCurrentUserId(userDetails)));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                projectService.getMyProjects(getCurrentUserId(userDetails)));
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<MemberResponse>> getMembers(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getMembers(projectId));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<String> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        projectService.addMember(
                projectId, request, getCurrentUserId(userDetails));
        return ResponseEntity.ok("Member added successfully");
    }

    // ✅ Returns descriptive message about task reassignment
    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String message = projectService.removeMember(
                projectId, memberId, getCurrentUserId(userDetails));
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{projectId}/available-users")
    public ResponseEntity<List<UserResponse>> getAvailableUsers(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(
                projectService.getAvailableUsers(projectId));
    }
}