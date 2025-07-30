package com.example.taskapi.service;

import com.example.taskapi.entity.Notification;
import com.example.taskapi.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getNotification(UUID id) {
        return notificationRepository.findById(id);
    }

    public Page<Notification> listNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    public List<Notification> listNotificationsByRecipient(UUID recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }

    public Notification markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }
}
