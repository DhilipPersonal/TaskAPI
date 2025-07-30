package com.example.taskapi.controller.v1;

import com.example.taskapi.entity.Task;
import com.example.taskapi.service.TaskService;
import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        Task task = mapToEntity(request);
        Task created = taskService.createTask(task);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable UUID id) {
        Optional<Task> task = taskService.getTask(id);
        return task.map(t -> ResponseEntity.ok(mapToResponse(t)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID parentTaskId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks;
        if (status != null) {
            tasks = taskService.listTasksByStatus(status, pageable);
        } else if (assignedTo != null) {
            tasks = taskService.listTasksByAssignedTo(assignedTo, pageable);
        } else if (projectId != null) {
            tasks = taskService.listTasksByProjectId(projectId, pageable);
        } else if (parentTaskId != null) {
            tasks = taskService.listSubTasks(parentTaskId, pageable);
        } else {
            tasks = taskService.listTasks(pageable);
        }
        return ResponseEntity.ok(tasks.map(this::mapToResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID id, @RequestBody TaskRequest request) {
        Task task = mapToEntity(request);
        task.setId(id);
        Task updated = taskService.updateTask(task);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Mapping helpers
    private Task mapToEntity(TaskRequest req) {
        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setStatus(req.getStatus());
        t.setPriority(req.getPriority());
        t.setDueDate(req.getDueDate());
        t.setAssignedTo(req.getAssignedTo());
        t.setProjectId(req.getProjectId());
        t.setParentTaskId(req.getParentTaskId());
        t.setDependencies(req.getDependencies());
        t.setRecurrence(req.getRecurrence());
        return t;
    }
    private TaskResponse mapToResponse(Task t) {
        TaskResponse res = new TaskResponse();
        res.setId(t.getId());
        res.setTitle(t.getTitle());
        res.setDescription(t.getDescription());
        res.setStatus(t.getStatus());
        res.setPriority(t.getPriority());
        res.setDueDate(t.getDueDate());
        res.setCreatedAt(t.getCreatedAt());
        res.setUpdatedAt(t.getUpdatedAt());
        res.setAssignedTo(t.getAssignedTo());
        res.setProjectId(t.getProjectId());
        res.setParentTaskId(t.getParentTaskId());
        res.setDependencies(t.getDependencies());
        res.setRecurrence(t.getRecurrence());
        return res;
    }
}

