package com.example.taskapi.controller.v1;

import com.example.taskapi.entity.Project;
import com.example.taskapi.service.ProjectService;
import com.example.taskapi.dto.ProjectRequest;
import com.example.taskapi.dto.ProjectResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request) {
        Project project = mapToEntity(request);
        Project created = projectService.createProject(project);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID id) {
        Optional<Project> project = projectService.getProject(id);
        return project.map(p -> ResponseEntity.ok(mapToResponse(p)))
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ProjectResponse>> listProjects(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projects = projectService.listProjects(pageable);
        return ResponseEntity.ok(projects.map(this::mapToResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID id, @RequestBody ProjectRequest request) {
        Project project = mapToEntity(request);
        project.setId(id);
        Project updated = projectService.updateProject(project);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> addMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        Project updated = projectService.addMember(id, memberId);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> removeMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        Project updated = projectService.removeMember(id, memberId);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @PutMapping("/{id}/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> assignOwner(@PathVariable UUID id, @PathVariable UUID ownerId) {
        Project updated = projectService.assignOwner(id, ownerId);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // Mapping helpers
    private Project mapToEntity(ProjectRequest req) {
        Project p = new Project();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setStatus(req.getStatus());
        p.setStartDate(req.getStartDate());
        p.setEndDate(req.getEndDate());
        p.setMemberIds(req.getMemberIds());
        p.setOwnerId(req.getOwnerId());
        return p;
    }
    private ProjectResponse mapToResponse(Project p) {
        ProjectResponse res = new ProjectResponse();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setDescription(p.getDescription());
        res.setStatus(p.getStatus());
        res.setStartDate(p.getStartDate());
        res.setEndDate(p.getEndDate());
        res.setMemberIds(p.getMemberIds());
        res.setOwnerId(p.getOwnerId());
        return res;
    }
}
