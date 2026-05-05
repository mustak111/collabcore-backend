package com.mustak.taskmanager.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberId implements Serializable {

    private Long projectId;
    private Long userId;
}