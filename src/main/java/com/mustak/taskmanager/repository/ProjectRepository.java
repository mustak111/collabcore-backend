package com.mustak.taskmanager.repository;

import com.mustak.taskmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p FROM Project p
        JOIN ProjectMember pm ON pm.project.id = p.id
        WHERE pm.user.id = :userId
    """)
    List<Project> findAllByMemberId(@Param("userId") Long userId);
}