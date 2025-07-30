package com.example.taskapi.service;

import com.example.taskapi.entity.Comment;
import com.example.taskapi.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Optional<Comment> getComment(UUID id) {
        return commentRepository.findById(id);
    }

    public Page<Comment> listComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public List<Comment> listCommentsByTask(UUID taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    public Comment updateComment(Comment comment) {
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }
}
