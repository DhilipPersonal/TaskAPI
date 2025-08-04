package com.example.taskapi.service;

import com.example.taskapi.dto.analytics.*;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.ProjectRepository;
import com.example.taskapi.repository.UserRepository;

import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    

    public ProductivityMetricsResponse getProductivityMetrics() {
        ProductivityMetricsResponse resp = new ProductivityMetricsResponse();
        resp.setCompletedTasks((int) taskRepository.findAll().stream().filter(t -> "COMPLETED".equals(t.getStatus())).count());
        resp.setTasksCreated((int) taskRepository.count());
        resp.setTasksCompletedToday((int) taskRepository.findAll().stream().filter(t -> "COMPLETED".equals(t.getStatus()) && t.getUpdatedAt() != null && t.getUpdatedAt().toLocalDate().equals(java.time.LocalDate.now())).count());
        resp.setTasksCompletedThisWeek((int) taskRepository.findAll().stream().filter(t -> "COMPLETED".equals(t.getStatus()) && t.getUpdatedAt() != null && t.getUpdatedAt().toLocalDate().isAfter(java.time.LocalDate.now().minusDays(7))).count());
        // Average completion time in hours
        resp.setAvgCompletionTimeHours(taskRepository.findAll().stream()
            .filter(t -> "COMPLETED".equals(t.getStatus()) && t.getCreatedAt() != null && t.getUpdatedAt() != null)
            .mapToDouble(t -> java.time.Duration.between(t.getCreatedAt(), t.getUpdatedAt()).toHours())
            .average().orElse(0.0));
        return resp;
    }

    public ProjectSummaryResponse getProjectSummary() {
        ProjectSummaryResponse resp = new ProjectSummaryResponse();
        resp.setTotalProjects((int) projectRepository.count());
        resp.setActiveProjects((int) projectRepository.findAll().stream().filter(p -> "ACTIVE".equals(p.getStatus())).count());
        resp.setCompletedProjects((int) projectRepository.findAll().stream().filter(p -> "COMPLETED".equals(p.getStatus())).count());
        resp.setOverdueProjects((int) projectRepository.findAll().stream().filter(p -> {
            if (p.getEndDate() != null && "ACTIVE".equals(p.getStatus())) {
                return p.getEndDate().isBefore(java.time.LocalDateTime.now());
            }
            return false;
        }).count());
        return resp;
    }

    public OverdueTasksResponse getOverdueTasks() {
        OverdueTasksResponse resp = new OverdueTasksResponse();
        java.util.List<Task> overdue = taskRepository.findAll().stream()
            .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(java.time.LocalDateTime.now()) && !"COMPLETED".equals(t.getStatus()))
            .toList();
        resp.setOverdueTasksCount(overdue.size());
        java.util.List<OverdueTasksResponse.TaskSummary> summaries = new java.util.ArrayList<>();
        for (Task t : overdue) {
            OverdueTasksResponse.TaskSummary ts = new OverdueTasksResponse.TaskSummary();
            ts.setId(t.getId().toString());
            ts.setTitle(t.getTitle());
            ts.setDueDate(t.getDueDate() != null ? t.getDueDate().toLocalDate() : null);
            ts.setAssignee(t.getAssignedTo() != null ? t.getAssignedTo().toString() : null);
            summaries.add(ts);
        }
        resp.setOverdueTasks(summaries);
        return resp;
    }

    public TeamWorkloadResponse getTeamWorkload() {
        TeamWorkloadResponse resp = new TeamWorkloadResponse();
        java.util.List<User> users = userRepository.findAll();
        java.util.List<Task> allTasks = taskRepository.findAll();
        java.util.List<TeamWorkloadResponse.UserWorkload> userWorkloads = new java.util.ArrayList<>();
        for (User user : users) {
            TeamWorkloadResponse.UserWorkload uw = new TeamWorkloadResponse.UserWorkload();
            uw.setUserId(user.getId().toString());
            uw.setUserName(user.getUsername());
            uw.setAssignedTasks((int) allTasks.stream().filter(t -> user.getId().equals(t.getAssignedTo())).count());
            uw.setCompletedTasks((int) allTasks.stream().filter(t -> user.getId().equals(t.getAssignedTo()) && "COMPLETED".equals(t.getStatus())).count());
            uw.setOverdueTasks((int) allTasks.stream().filter(t -> user.getId().equals(t.getAssignedTo()) && t.getDueDate() != null && t.getDueDate().isBefore(java.time.LocalDateTime.now()) && !"COMPLETED".equals(t.getStatus())).count());
            userWorkloads.add(uw);
        }
        resp.setUserWorkloads(userWorkloads);
        return resp;
    }
}
