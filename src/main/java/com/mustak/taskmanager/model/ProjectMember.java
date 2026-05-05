package com.mustak.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN, MEMBER
    }
}