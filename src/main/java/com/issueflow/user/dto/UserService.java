package com.issueflow.user;

import com.issueflow.audit.AuditAction;
import com.issueflow.audit.AuditLogService;
import com.issueflow.common.ApiException;
import com.issueflow.user.dto.UserCreateRequest;
import com.issueflow.user.dto.UserResponse;
import com.issueflow.user.dto.UserUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService audit;

    public UserService(UserRepository repo,
                       PasswordEncoder passwordEncoder,
                       AuditLogService audit) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.audit = audit;
    }

    public UserResponse create(UserCreateRequest req) {
        if (repo.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (repo.existsByEmail(req.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setFullName(req.getFullName());
        u.setRole(req.getRole());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        repo.save(u);

        // AUDIT HOOK — USER_CREATE
        audit.record(
                u.getUsername(),
                AuditAction.USER_CREATE,
                "User",
                u.getId(),
                "Created user " + u.getUsername()
        );

        return UserResponse.from(u);
    }

    public UserResponse get(Long id) {
        return UserResponse.from(
                repo.findById(id).orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "User not found"))
        );
    }

    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse update(Long id, UserUpdateRequest req) {
        User u = repo.findById(id).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        u.setFullName(req.getFullName());
        u.setRole(req.getRole());

        repo.save(u);

        // AUDIT HOOK — USER_UPDATE
        audit.record(
                "SYSTEM",
                AuditAction.USER_UPDATE,
                "User",
                u.getId(),
                "Updated user " + u.getUsername()
        );

        return UserResponse.from(u);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        repo.deleteById(id);

        // AUDIT HOOK — USER_DELETE
        audit.record(
                "SYSTEM",
                AuditAction.USER_DELETE,
                "User",
                id,
                "Deleted user " + id
        );
    }

    public User loadEntity(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
