package com.issueflow.auth;

import com.issueflow.user.User;
import com.issueflow.user.UserRole;

public class MeResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;

    public static MeResponse from(User user) {
        MeResponse r = new MeResponse();
        r.id = user.getId();
        r.username = user.getUsername();
        r.email = user.getEmail();
        r.fullName = user.getFullName();
        r.role = user.getRole();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }
}
