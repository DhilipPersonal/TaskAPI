package com.example.taskapi.dto.analytics;

import java.util.List;

public class TeamWorkloadResponse {
    private List<UserWorkload> userWorkloads;

    public TeamWorkloadResponse() {}

    public List<UserWorkload> getUserWorkloads() { return userWorkloads; }
    public void setUserWorkloads(List<UserWorkload> userWorkloads) { this.userWorkloads = userWorkloads; }

    public static class UserWorkload {
        private String userId;
        private String userName;
        private int assignedTasks;
        private int completedTasks;
        private int overdueTasks;

        public UserWorkload() {}
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public int getAssignedTasks() { return assignedTasks; }
        public void setAssignedTasks(int assignedTasks) { this.assignedTasks = assignedTasks; }
        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
        public int getOverdueTasks() { return overdueTasks; }
        public void setOverdueTasks(int overdueTasks) { this.overdueTasks = overdueTasks; }
    }
}
