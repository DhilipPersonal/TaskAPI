package com.example.taskapi.repository;

import com.example.taskapi.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
}
