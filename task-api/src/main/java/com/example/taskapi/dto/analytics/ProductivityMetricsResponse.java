package com.example.taskapi.dto.analytics;

public class ProductivityMetricsResponse {
    private int completedTasks;
    private int tasksCreated;
    private double avgCompletionTimeHours;
    private int tasksCompletedToday;
    private int tasksCompletedThisWeek;

    public ProductivityMetricsResponse() {}

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    public int getTasksCreated() { return tasksCreated; }
    public void setTasksCreated(int tasksCreated) { this.tasksCreated = tasksCreated; }
    public double getAvgCompletionTimeHours() { return avgCompletionTimeHours; }
    public void setAvgCompletionTimeHours(double avgCompletionTimeHours) { this.avgCompletionTimeHours = avgCompletionTimeHours; }
    public int getTasksCompletedToday() { return tasksCompletedToday; }
    public void setTasksCompletedToday(int tasksCompletedToday) { this.tasksCompletedToday = tasksCompletedToday; }
    public int getTasksCompletedThisWeek() { return tasksCompletedThisWeek; }
    public void setTasksCompletedThisWeek(int tasksCompletedThisWeek) { this.tasksCompletedThisWeek = tasksCompletedThisWeek; }
}
