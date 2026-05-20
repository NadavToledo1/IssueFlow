package com.issueflow.project.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
