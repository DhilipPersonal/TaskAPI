package com.example.taskapi.controller.v1;

import com.example.taskapi.entity.Team;
import com.example.taskapi.service.TeamService;
import com.example.taskapi.dto.TeamRequest;
import com.example.taskapi.dto.TeamResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<TeamResponse> createTeam(@RequestBody TeamRequest request) {
        Team team = mapToEntity(request);
        Team created = teamService.createTeam(team);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable UUID id) {
        Optional<Team> team = teamService.getTeam(id);
        return team.map(t -> ResponseEntity.ok(mapToResponse(t)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TeamResponse>> listTeams(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Team> teams = teamService.listTeams(pageable);
        return ResponseEntity.ok(teams.map(this::mapToResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable UUID id, @RequestBody TeamRequest request) {
        Team team = mapToEntity(request);
        team.setId(id);
        Team updated = teamService.updateTeam(team);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<TeamResponse> addMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        Team updated = teamService.addMember(id, memberId);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
    public ResponseEntity<TeamResponse> removeMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        Team updated = teamService.removeMember(id, memberId);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // Mapping helpers
    private Team mapToEntity(TeamRequest req) {
        Team t = new Team();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setMemberIds(req.getMemberIds());
        return t;
    }
    private TeamResponse mapToResponse(Team t) {
        TeamResponse res = new TeamResponse();
        res.setId(t.getId());
        res.setName(t.getName());
        res.setDescription(t.getDescription());
        res.setMemberIds(t.getMemberIds());
        return res;
    }
}
