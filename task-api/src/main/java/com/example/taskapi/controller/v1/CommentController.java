package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.CommentRequest;
import com.example.taskapi.dto.CommentResponse;
import com.example.taskapi.entity.Comment;
import com.example.taskapi.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        Comment comment = mapToEntity(request);
        Comment created = commentService.createComment(comment);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> getComment(@PathVariable UUID id) {
        Optional<Comment> comment = commentService.getComment(id);
        return comment.map(c -> ResponseEntity.ok(mapToResponse(c)))
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CommentResponse>> listComments(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.listComments(pageable);
        return ResponseEntity.ok(comments.map(this::mapToResponse));
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentResponse>> listCommentsByTask(@PathVariable UUID taskId) {
        List<Comment> comments = commentService.listCommentsByTask(taskId);
        return ResponseEntity.ok(comments.stream().map(this::mapToResponse).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable UUID id, @RequestBody CommentRequest request) {
        Comment comment = mapToEntity(request);
        comment.setId(id);
        Comment updated = commentService.updateComment(comment);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // Mapping helpers
    private Comment mapToEntity(CommentRequest req) {
        Comment c = new Comment();
        c.setTaskId(req.getTaskId());
        c.setAuthorId(req.getAuthorId());
        c.setContent(req.getContent());
        return c;
    }
    private CommentResponse mapToResponse(Comment c) {
        CommentResponse res = new CommentResponse();
        res.setId(c.getId());
        res.setTaskId(c.getTaskId());
        res.setAuthorId(c.getAuthorId());
        res.setContent(c.getContent());
        res.setCreatedAt(c.getCreatedAt());
        res.setUpdatedAt(c.getUpdatedAt());
        return res;
    }
}
