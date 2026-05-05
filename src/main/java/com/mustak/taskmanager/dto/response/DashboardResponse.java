package com.mustak.taskmanager.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

    private long totalTasks;
    private long overdueTasks;
    private Map<String, Long> tasksByStatus;
    private List<UserTaskCount> tasksByUser;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserTaskCount {
        private String userName;
        private Long count;
    }
}