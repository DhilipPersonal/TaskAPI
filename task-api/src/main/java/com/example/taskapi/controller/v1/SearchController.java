package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.entity.Task;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    @Autowired
    private TaskRepository taskRepository;

    // Global search (title/description full-text)
    @GetMapping("/tasks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskResponse>> globalSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> results = taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
        return ResponseEntity.ok(results.map(this::mapToResponse));
    }

    // Advanced search with filters
    @GetMapping("/tasks/advanced")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskResponse>> advancedSearch(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID assignee,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Advanced filtering using combinations of repository methods
        Page<Task> results;
        if (status != null) {
            results = taskRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            results = taskRepository.findByPriority(priority, pageable);
        } else if (assignee != null) {
            results = taskRepository.findByAssignedTo(assignee, pageable);
        } else if (projectId != null) {
            results = taskRepository.findByProjectId(projectId, pageable);
        } else if (tag != null) {
            results = taskRepository.findByTagsContaining(tag, pageable);
        } else if (q != null) {
            results = taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
        } else {
            results = taskRepository.findAll(pageable);
        }
        // Further manual filter for date range if needed (not shown)
        return ResponseEntity.ok(results.map(this::mapToResponse));
    }

    // Mapping helper (reuse from TaskController if available)
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
        res.setTags(t.getTags());
        return res;
    }
}
