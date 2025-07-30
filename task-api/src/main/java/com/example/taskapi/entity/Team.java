package com.example.taskapi.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teams", schema = "taskapp")
public class Team {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ElementCollection
    private Set<UUID> memberIds = new HashSet<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<UUID> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<UUID> memberIds) { this.memberIds = memberIds; }
}

