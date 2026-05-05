package com.mustak.taskmanager.service;

import com.mustak.taskmanager.dto.response.DashboardResponse;
import com.mustak.taskmanager.model.Task;
import com.mustak.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TaskRepository taskRepository;

    public DashboardResponse getDashboard(Long projectId) {

        long total = taskRepository.countByProjectId(projectId);

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Task.Status status : Task.Status.values()) {
            byStatus.put(status.name(),
                    taskRepository.countByProjectIdAndStatus(
                            projectId, status));
        }

        long overdue = taskRepository
                .countByProjectIdAndDueDateBeforeAndStatusNot(
                        projectId, LocalDate.now(), Task.Status.DONE);

        List<DashboardResponse.UserTaskCount> byUser =
                taskRepository.countTasksGroupedByUser(projectId)
                        .stream()
                        .map(row -> new DashboardResponse.UserTaskCount(
                                (String) row[0], (Long) row[1]))
                        .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalTasks(total)
                .overdueTasks(overdue)
                .tasksByStatus(byStatus)
                .tasksByUser(byUser)
                .build();
    }
}