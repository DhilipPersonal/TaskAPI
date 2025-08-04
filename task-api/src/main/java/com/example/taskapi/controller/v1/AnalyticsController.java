package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.analytics.*;
import com.example.taskapi.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @GetMapping("/productivity-metrics")
    public ResponseEntity<ProductivityMetricsResponse> getProductivityMetrics() {
        return ResponseEntity.ok(analyticsService.getProductivityMetrics());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @GetMapping("/project-summary")
    public ResponseEntity<ProjectSummaryResponse> getProjectSummary() {
        return ResponseEntity.ok(analyticsService.getProjectSummary());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @GetMapping("/overdue-tasks")
    public ResponseEntity<OverdueTasksResponse> getOverdueTasks() {
        return ResponseEntity.ok(analyticsService.getOverdueTasks());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @GetMapping("/team-workload")
    public ResponseEntity<TeamWorkloadResponse> getTeamWorkload() {
        return ResponseEntity.ok(analyticsService.getTeamWorkload());
    }
}
