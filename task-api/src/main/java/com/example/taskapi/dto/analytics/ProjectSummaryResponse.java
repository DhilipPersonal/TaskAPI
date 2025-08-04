package com.example.taskapi.dto.analytics;

public class ProjectSummaryResponse {
    private int totalProjects;
    private int activeProjects;
    private int completedProjects;
    private int overdueProjects;

    public ProjectSummaryResponse() {}

    public int getTotalProjects() { return totalProjects; }
    public void setTotalProjects(int totalProjects) { this.totalProjects = totalProjects; }
    public int getActiveProjects() { return activeProjects; }
    public void setActiveProjects(int activeProjects) { this.activeProjects = activeProjects; }
    public int getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(int completedProjects) { this.completedProjects = completedProjects; }
    public int getOverdueProjects() { return overdueProjects; }
    public void setOverdueProjects(int overdueProjects) { this.overdueProjects = overdueProjects; }
}
