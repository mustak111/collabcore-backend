package com.mustak.taskmanager.repository;

import com.mustak.taskmanager.model.ProjectMember;
import com.mustak.taskmanager.model.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, ProjectMemberId> {

    // ✅ Explicit JPQL — avoids EmbeddedId resolution issues
    @Query("SELECT pm FROM ProjectMember pm " +
            "LEFT JOIN FETCH pm.user " +
            "WHERE pm.project.id = :projectId")
    List<ProjectMember> findAllByProjectId(
            @Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm " +
            "WHERE pm.project.id = :projectId " +
            "AND pm.user.id = :userId")
    Optional<ProjectMember> findByProjectIdAndUserId(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(pm) > 0 " +
            "THEN true ELSE false END " +
            "FROM ProjectMember pm " +
            "WHERE pm.project.id = :projectId " +
            "AND pm.user.id = :userId " +
            "AND pm.role = :role")
    boolean existsByProjectIdAndUserIdAndRole(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId,
            @Param("role") ProjectMember.Role role);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProjectMember pm " +
            "WHERE pm.project.id = :projectId " +
            "AND pm.user.id = :userId")
    void deleteByProjectIdAndUserId(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId);
}