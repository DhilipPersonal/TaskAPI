package com.example.taskapi.controller.v1;

import com.example.taskapi.dto.NotificationPreferenceRequest;
import com.example.taskapi.dto.NotificationPreferenceResponse;
import com.example.taskapi.entity.NotificationPreference;
import com.example.taskapi.service.NotificationPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification-preferences")
public class NotificationPreferenceController {
    @Autowired
    private NotificationPreferenceService service;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationPreferenceResponse> get(@PathVariable UUID userId) {
        Optional<NotificationPreference> pref = service.get(userId);
        return pref.map(p -> ResponseEntity.ok(mapToResponse(p)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationPreferenceResponse> update(@PathVariable UUID userId, @RequestBody NotificationPreferenceRequest req) {
        NotificationPreference pref = mapToEntity(req);
        pref.setUserId(userId);
        NotificationPreference saved = service.save(pref);
        return ResponseEntity.ok(mapToResponse(saved));
    }

    // Mapping helpers
    private NotificationPreference mapToEntity(NotificationPreferenceRequest req) {
        NotificationPreference p = new NotificationPreference();
        p.setUserId(req.getUserId());
        p.setTaskComment(req.isTaskComment());
        p.setTaskAssigned(req.isTaskAssigned());
        p.setGeneral(req.isGeneral());
        return p;
    }
    private NotificationPreferenceResponse mapToResponse(NotificationPreference p) {
        NotificationPreferenceResponse res = new NotificationPreferenceResponse();
        res.setUserId(p.getUserId());
        res.setTaskComment(p.isTaskComment());
        res.setTaskAssigned(p.isTaskAssigned());
        res.setGeneral(p.isGeneral());
        return res;
    }
}
