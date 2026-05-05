package com.mustak.taskmanager.service;

import com.mustak.taskmanager.dto.request.AddMemberRequest;
import com.mustak.taskmanager.dto.request.ProjectRequest;
import com.mustak.taskmanager.dto.response.MemberResponse;
import com.mustak.taskmanager.dto.response.ProjectResponse;
import com.mustak.taskmanager.dto.response.UserResponse;
import com.mustak.taskmanager.exception.ResourceNotFoundException;
import com.mustak.taskmanager.exception.UnauthorizedException;
import com.mustak.taskmanager.model.*;
import com.mustak.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository    projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository       userRepository;
    private final TaskRepository       taskRepository;

    // ─────────────────────────────────────────────────────────────────
    @Transactional
    public ProjectResponse createProject(ProjectRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", adminId));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .admin(admin)
                .build();
        project = projectRepository.save(project);

        memberRepository.save(ProjectMember.builder()
                .id(new ProjectMemberId(project.getId(), adminId))
                .project(project)
                .user(admin)
                .role(ProjectMember.Role.ADMIN)
                .build());

        return mapToResponse(project, "ADMIN");
    }

    // ─────────────────────────────────────────────────────────────────
    public List<ProjectResponse> getMyProjects(Long userId) {
        return projectRepository.findAllByMemberId(userId).stream()
                .map(p -> {
                    ProjectMember pm = memberRepository
                            .findByProjectIdAndUserId(p.getId(), userId)
                            .orElse(null);
                    String role = pm != null ? pm.getRole().name() : "MEMBER";
                    return mapToResponse(p, role);
                })
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────
    @Transactional
    public void addMember(Long projectId, AddMemberRequest request, Long adminId) {
        validateAdmin(projectId, adminId);

        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + request.getEmail()));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        if (memberRepository.findByProjectIdAndUserId(
                projectId, newMember.getId()).isPresent()) {
            throw new RuntimeException("User is already a member");
        }

        memberRepository.save(ProjectMember.builder()
                .id(new ProjectMemberId(projectId, newMember.getId()))
                .project(project)
                .user(newMember)
                .role(ProjectMember.Role.MEMBER)
                .build());
    }

    // ─────────────────────────────────────────────────────────────────
    // ✅ AUTO-REASSIGN: when member removed, their tasks → Admin
    @Transactional
    public String removeMember(Long projectId, Long userId, Long adminId) {
        validateAdmin(projectId, adminId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        User admin = project.getAdmin();

        // 1️⃣ Find ALL tasks assigned to this user in this project
        List<Task> userTasks = taskRepository
                .findAllByProjectIdAndAssigneeId(projectId, userId);

        // 2️⃣ Find OVERDUE + incomplete tasks
        List<Task> overdueTasks = userTasks.stream()
                .filter(t -> t.getStatus() != Task.Status.DONE)
                .filter(t -> t.getDueDate() != null
                        && t.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        // 3️⃣ Find non-overdue incomplete tasks
        List<Task> pendingTasks = userTasks.stream()
                .filter(t -> t.getStatus() != Task.Status.DONE)
                .filter(t -> t.getDueDate() == null
                        || !t.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        int overdueCount  = overdueTasks.size();
        int pendingCount  = pendingTasks.size();

        // 4️⃣ Reassign ALL incomplete tasks to Admin
        userTasks.stream()
                .filter(t -> t.getStatus() != Task.Status.DONE)
                .forEach(t -> {
                    t.setAssignee(admin);
                    taskRepository.save(t);
                });

        // 5️⃣ Remove the member
        memberRepository.deleteByProjectIdAndUserId(projectId, userId);

        // 6️⃣ Return informative message
        StringBuilder msg = new StringBuilder("Member removed successfully.");
        if (overdueCount > 0) {
            msg.append(String.format(
                    " ⚠️ %d overdue task(s) auto-reassigned to Admin.", overdueCount));
        }
        if (pendingCount > 0) {
            msg.append(String.format(
                    " 📌 %d pending task(s) auto-reassigned to Admin.", pendingCount));
        }
        return msg.toString();
    }

    // ─────────────────────────────────────────────────────────────────
    public List<MemberResponse> getMembers(Long projectId) {
        return memberRepository.findAllByProjectId(projectId).stream()
                .map(pm -> MemberResponse.builder()
                        .userId(pm.getUser().getId())
                        .name(pm.getUser().getName())
                        .email(pm.getUser().getEmail())
                        .role(pm.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────
    public List<UserResponse> getAvailableUsers(Long projectId) {
        Set<Long> memberIds = memberRepository
                .findAllByProjectId(projectId).stream()
                .map(pm -> pm.getUser().getId())
                .collect(Collectors.toSet());

        return userRepository.findAll().stream()
                .filter(u -> !memberIds.contains(u.getId()))
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────
    private void validateAdmin(Long projectId, Long userId) {
        if (!memberRepository.existsByProjectIdAndUserIdAndRole(
                projectId, userId, ProjectMember.Role.ADMIN)) {
            throw new UnauthorizedException("Admin access required");
        }
    }

    private ProjectResponse mapToResponse(Project p, String role) {
        return ProjectResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .adminName(p.getAdmin().getName())
                .role(role)
                .createdAt(p.getCreatedAt())
                .build();
    }
}