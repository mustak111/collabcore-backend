package com.mustak.taskmanager.repository;

import com.mustak.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProjectId(Long projectId);

    List<Task> findAllByProjectIdAndAssigneeId(Long projectId, Long userId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, Task.Status status);

    long countByProjectIdAndDueDateBeforeAndStatusNot(
            Long projectId, LocalDate date, Task.Status status);

    @Query("""
        SELECT t.assignee.name, COUNT(t)
        FROM Task t
        WHERE t.project.id = :projectId
        AND t.assignee IS NOT NULL
        GROUP BY t.assignee.id, t.assignee.name
    """)
    List<Object[]> countTasksGroupedByUser(@Param("projectId") Long projectId);
}