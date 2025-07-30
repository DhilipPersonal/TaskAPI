package com.example.taskapi.service;

import com.example.taskapi.entity.Project;
import com.example.taskapi.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> getProject(UUID id) {
        return projectRepository.findById(id);
    }

    public Page<Project> listProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }

    public Project addMember(UUID projectId, UUID memberId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Set<UUID> members = project.getMemberIds();
        members.add(memberId);
        project.setMemberIds(members);
        return projectRepository.save(project);
    }

    public Project removeMember(UUID projectId, UUID memberId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Set<UUID> members = project.getMemberIds();
        members.remove(memberId);
        project.setMemberIds(members);
        return projectRepository.save(project);
    }

    public Project assignOwner(UUID projectId, UUID ownerId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        project.setOwnerId(ownerId);
        return projectRepository.save(project);
    }
}
