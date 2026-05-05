package com.mustak.taskmanager.service;

import com.mustak.taskmanager.dto.request.TaskRequest;
import com.mustak.taskmanager.dto.response.TaskResponse;
import com.mustak.taskmanager.exception.ResourceNotFoundException;
import com.mustak.taskmanager.exception.UnauthorizedException;
import com.mustak.taskmanager.model.*;
import com.mustak.taskmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository memberRepository;

    public TaskResponse createTask(Long projectId,
                                   TaskRequest request,
                                   Long creatorId) {
        validateAdmin(projectId, creatorId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project", projectId));

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", creatorId));

        User assignee = null;
        if (request.getAssignedTo() != null) {
            assignee = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Assignee", request.getAssignedTo()));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .priority(request.getPriority() != null
                        ? request.getPriority() : Task.Priority.MEDIUM)
                .status(Task.Status.TODO)
                .project(project)
                .assignee(assignee)
                .createdBy(creator)
                .build();

        return mapToResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getProjectTasks(Long projectId, Long userId) {
        ProjectMember membership = memberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new UnauthorizedException("Not a project member"));

        List<Task> tasks;
        if (membership.getRole() == ProjectMember.Role.ADMIN) {
            tasks = taskRepository.findAllByProjectId(projectId);
        } else {
            tasks = taskRepository
                    .findAllByProjectIdAndAssigneeId(projectId, userId);
        }

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long taskId,
                                   TaskRequest request,
                                   Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task", taskId));

        ProjectMember membership = memberRepository
                .findByProjectIdAndUserId(
                        task.getProject().getId(), userId)
                .orElseThrow(() ->
                        new UnauthorizedException("Not a project member"));

        if (membership.getRole() == ProjectMember.Role.MEMBER) {
            if (task.getAssignee() == null
                    || !task.getAssignee().getId().equals(userId)) {
                throw new UnauthorizedException(
                        "You can only update your own assigned tasks");
            }
            if (request.getStatus() != null) {
                task.setStatus(request.getStatus());
            }
        } else {
            if (request.getTitle() != null)
                task.setTitle(request.getTitle());
            if (request.getDescription() != null)
                task.setDescription(request.getDescription());
            if (request.getDueDate() != null)
                task.setDueDate(request.getDueDate());
            if (request.getPriority() != null)
                task.setPriority(request.getPriority());
            if (request.getStatus() != null)
                task.setStatus(request.getStatus());
            if (request.getAssignedTo() != null) {
                User assignee = userRepository
                        .findById(request.getAssignedTo())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignee",
                                        request.getAssignedTo()));
                task.setAssignee(assignee);
            }
        }

        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task", taskId));
        validateAdmin(task.getProject().getId(), userId);
        taskRepository.delete(task);
    }

    private void validateAdmin(Long projectId, Long userId) {
        boolean isAdmin = memberRepository
                .existsByProjectIdAndUserIdAndRole(
                        projectId, userId, ProjectMember.Role.ADMIN);
        if (!isAdmin) {
            throw new UnauthorizedException("Admin access required");
        }
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .status(task.getStatus())
                .assigneeName(task.getAssignee() != null
                        ? task.getAssignee().getName() : null)
                .assigneeId(task.getAssignee() != null
                        ? task.getAssignee().getId() : null)
                .createdByName(task.getCreatedBy().getName())
                .createdAt(task.getCreatedAt())
                .build();
    }
}