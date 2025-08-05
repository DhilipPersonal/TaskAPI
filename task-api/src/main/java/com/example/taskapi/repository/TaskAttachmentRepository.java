package com.example.taskapi.repository;

import com.example.taskapi.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    
    List<TaskAttachment> findByTaskId(UUID taskId);
    
    @Query("SELECT ta FROM TaskAttachment ta WHERE ta.task.id = :taskId ORDER BY ta.uploadedAt DESC")
    List<TaskAttachment> findByTaskIdOrderByUploadedAtDesc(@Param("taskId") UUID taskId);
    
    @Query("SELECT ta FROM TaskAttachment ta WHERE ta.uploadedBy.id = :userId ORDER BY ta.uploadedAt DESC")
    List<TaskAttachment> findByUploadedByIdOrderByUploadedAtDesc(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(ta) FROM TaskAttachment ta WHERE ta.task.id = :taskId")
    long countByTaskId(@Param("taskId") UUID taskId);
    
    @Query("SELECT SUM(ta.fileSize) FROM TaskAttachment ta WHERE ta.task.id = :taskId")
    Long getTotalFileSizeByTaskId(@Param("taskId") UUID taskId);
}
