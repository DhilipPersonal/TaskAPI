package com.example.taskapi.service;

import com.example.taskapi.entity.NotificationPreference;
import com.example.taskapi.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationPreferenceService {
    @Autowired
    private NotificationPreferenceRepository repository;

    public NotificationPreference save(NotificationPreference preference) {
        return repository.save(preference);
    }

    public Optional<NotificationPreference> get(UUID userId) {
        return repository.findById(userId);
    }
}
