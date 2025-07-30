package com.example.taskapi.service;

import com.example.taskapi.entity.Team;
import com.example.taskapi.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public Optional<Team> getTeam(UUID id) {
        return teamRepository.findById(id);
    }

    public Page<Team> listTeams(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    public Team addMember(UUID teamId, UUID memberId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        Set<UUID> members = team.getMemberIds();
        members.add(memberId);
        team.setMemberIds(members);
        return teamRepository.save(team);
    }

    public Team removeMember(UUID teamId, UUID memberId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        Set<UUID> members = team.getMemberIds();
        members.remove(memberId);
        team.setMemberIds(members);
        return teamRepository.save(team);
    }
}
