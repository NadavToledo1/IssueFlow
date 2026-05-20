package com.issueflow.project.dto;

import com.issueflow.project.Project;

public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private boolean deleted;

    public static ProjectResponse from(Project p) {
        ProjectResponse r = new ProjectResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.description = p.getDescription();
        r.ownerId = p.getOwner().getId();
        r.deleted = p.isDeleted();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
