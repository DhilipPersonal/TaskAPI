package com.example.taskapi.dto.analytics;

import java.util.List;
import java.time.LocalDate;

public class OverdueTasksResponse {
    private int overdueTasksCount;
    private List<TaskSummary> overdueTasks;

    public OverdueTasksResponse() {}

    public int getOverdueTasksCount() { return overdueTasksCount; }
    public void setOverdueTasksCount(int overdueTasksCount) { this.overdueTasksCount = overdueTasksCount; }
    public List<TaskSummary> getOverdueTasks() { return overdueTasks; }
    public void setOverdueTasks(List<TaskSummary> overdueTasks) { this.overdueTasks = overdueTasks; }

    public static class TaskSummary {
        private String id;
        private String title;
        private LocalDate dueDate;
        private String assignee;

        public TaskSummary() {}
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }
    }
}
