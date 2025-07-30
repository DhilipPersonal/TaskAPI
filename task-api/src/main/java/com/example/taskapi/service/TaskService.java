package com.example.taskapi.service;

import com.example.taskapi.entity.Task;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> getTask(UUID id) {
        return taskRepository.findById(id);
    }

    public Page<Task> listTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> listTasksByStatus(String status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    public Page<Task> listTasksByAssignedTo(UUID assignedTo, Pageable pageable) {
        return taskRepository.findByAssignedTo(assignedTo, pageable);
    }

    public Page<Task> listTasksByProjectId(UUID projectId, Pageable pageable) {
        return taskRepository.findByProjectId(projectId, pageable);
    }

    public Page<Task> listSubTasks(UUID parentTaskId, Pageable pageable) {
        return taskRepository.findByParentTaskId(parentTaskId, pageable);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }
}
