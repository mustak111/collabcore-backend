package com.mustak.taskmanager.controller;

import com.mustak.taskmanager.dto.response.DashboardResponse;
import com.mustak.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(
                dashboardService.getDashboard(projectId));
    }
}