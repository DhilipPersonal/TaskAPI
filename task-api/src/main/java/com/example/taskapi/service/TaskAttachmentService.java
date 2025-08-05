package com.example.taskapi.service;

import com.example.taskapi.dto.TaskAttachmentResponse;
import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.TaskAttachment;
import com.example.taskapi.entity.User;
import com.example.taskapi.repository.TaskAttachmentRepository;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskAttachmentService {
    
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or @taskService.canUserAccessTask(#taskId, authentication.name)")
    public TaskAttachmentResponse uploadAttachment(UUID taskId, MultipartFile file, String description, String username) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Store the file
        String filePath = fileStorageService.storeFile(file);
        
        // Create attachment record
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName(filePath.substring(filePath.lastIndexOf('/') + 1));
        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setFilePath(filePath);
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setUploadedBy(user);
        attachment.setDescription(description);
        
        TaskAttachment savedAttachment = taskAttachmentRepository.save(attachment);
        
        log.info("File attachment uploaded for task {}: {}", taskId, file.getOriginalFilename());
        
        return convertToResponse(savedAttachment);
    }
    
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or @taskService.canUserAccessTask(#taskId, authentication.name)")
    public List<TaskAttachmentResponse> getTaskAttachments(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }
        
        return taskAttachmentRepository.findByTaskIdOrderByUploadedAtDesc(taskId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or @taskAttachmentRepository.findById(#attachmentId).map(a -> a.uploadedBy.username).orElse('') == authentication.name")
    public byte[] downloadAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found with id: " + attachmentId));
        
        return fileStorageService.getFile(attachment.getFilePath());
    }
    
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or @taskAttachmentRepository.findById(#attachmentId).map(a -> a.uploadedBy.username).orElse('') == authentication.name")
    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found with id: " + attachmentId));
        
        // Delete file from storage
        boolean fileDeleted = fileStorageService.deleteFile(attachment.getFilePath());
        if (!fileDeleted) {
            log.warn("Failed to delete file from storage: {}", attachment.getFilePath());
        }
        
        // Delete attachment record
        taskAttachmentRepository.delete(attachment);
        
        log.info("Attachment deleted: {} ({})", attachment.getOriginalFileName(), attachmentId);
    }
    
    public TaskAttachmentResponse getAttachmentInfo(Long attachmentId) {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found with id: " + attachmentId));
        
        return convertToResponse(attachment);
    }
    
    public List<TaskAttachmentResponse> getUserAttachments(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return taskAttachmentRepository.findByUploadedByIdOrderByUploadedAtDesc(user.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private TaskAttachmentResponse convertToResponse(TaskAttachment attachment) {
        TaskAttachmentResponse response = new TaskAttachmentResponse();
        response.setId(attachment.getId());
        response.setTaskId(attachment.getTask().getId());
        response.setFileName(attachment.getOriginalFileName());
        response.setContentType(attachment.getContentType());
        response.setFileSize(attachment.getFileSize());
        response.setUploadedBy(attachment.getUploadedBy().getUsername());
        response.setUploadedAt(attachment.getUploadedAt());
        response.setDescription(attachment.getDescription());
        return response;
    }
}
