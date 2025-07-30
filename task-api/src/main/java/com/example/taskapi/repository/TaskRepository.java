package com.example.taskapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.taskapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    // Filtering by status
    Page<Task> findByStatus(String status, Pageable pageable);
    // Filtering by assigned user
    Page<Task> findByAssignedTo(UUID assignedTo, Pageable pageable);
    // Filtering by project
    Page<Task> findByProjectId(UUID projectId, Pageable pageable);
    // Filtering by parent task (for sub-tasks)
    Page<Task> findByParentTaskId(UUID parentTaskId, Pageable pageable);
    // Global search (title/description)
    Page<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);

    // Filtering by priority
    Page<Task> findByPriority(String priority, Pageable pageable);

    // Filtering by tags (assuming tags is a collection)
    Page<Task> findByTagsContaining(String tag, Pageable pageable);

    // Advanced search placeholder (custom @Query)
    // For full dynamic filtering, implement a custom repository or use Specification/Criteria API
    // Example signature (implementation needed):
    // Page<Task> advancedSearch(String status, String priority, UUID assignee, UUID projectId, String tag, String q, String fromDate, String toDate, Pageable pageable);
}

