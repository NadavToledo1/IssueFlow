package com.issueflow.user.dto;

import com.issueflow.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserUpdateRequest {

    @NotBlank
    private String fullName;

    @NotNull
    private UserRole role;

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }
}
