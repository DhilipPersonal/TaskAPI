package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.NotificationRequest;
import com.example.taskapi.dto.NotificationResponse;
import com.example.taskapi.entity.Notification;
import com.example.taskapi.service.NotificationService;
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
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) {
        Notification notification = mapToEntity(request);
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable UUID id) {
        Optional<Notification> notification = notificationService.getNotification(id);
        return notification.map(n -> ResponseEntity.ok(mapToResponse(n)))
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationResponse>> listNotifications(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.listNotifications(pageable);
        return ResponseEntity.ok(notifications.map(this::mapToResponse));
    }

    @GetMapping("/recipient/{recipientId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationResponse>> listNotificationsByRecipient(@PathVariable UUID recipientId) {
        List<Notification> notifications = notificationService.listNotificationsByRecipient(recipientId);
        return ResponseEntity.ok(notifications.stream().map(this::mapToResponse).toList());
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        Notification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // Mapping helpers
    private Notification mapToEntity(NotificationRequest req) {
        Notification n = new Notification();
        n.setRecipientId(req.getRecipientId());
        n.setType(req.getType());
        n.setMessage(req.getMessage());
        return n;
    }
    private NotificationResponse mapToResponse(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setId(n.getId());
        res.setRecipientId(n.getRecipientId());
        res.setType(n.getType());
        res.setMessage(n.getMessage());
        res.setRead(n.isRead());
        res.setCreatedAt(n.getCreatedAt());
        return res;
    }
}
