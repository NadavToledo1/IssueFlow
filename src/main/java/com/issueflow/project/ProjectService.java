package com.issueflow.project;

import com.issueflow.audit.AuditAction;
import com.issueflow.audit.AuditLogService;
import com.issueflow.common.ApiException;
import com.issueflow.project.dto.ProjectCreateRequest;
import com.issueflow.project.dto.ProjectResponse;
import com.issueflow.project.dto.ProjectUpdateRequest;
import com.issueflow.user.User;
import com.issueflow.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repo;
    private final UserService userService;
    private final AuditLogService audit;

    public ProjectService(ProjectRepository repo,
                          UserService userService,
                          AuditLogService audit) {
        this.repo = repo;
        this.userService = userService;
        this.audit = audit;
    }

    public ProjectResponse create(ProjectCreateRequest req) {
        User owner = userService.loadEntity(req.getOwnerId());

        Project p = new Project();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setOwner(owner);

        repo.save(p);

        // AUDIT HOOK — PROJECT_CREATE
        audit.record(
                owner.getUsername(),
                AuditAction.PROJECT_CREATE,
                "Project",
                p.getId(),
                "Created project " + p.getName()
        );

        return ProjectResponse.from(p);
    }

    public Project loadEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public ProjectResponse get(Long id) {
        Project p = loadEntity(id);
        if (p.isDeleted()) {
            throw new ApiException(HttpStatus.GONE, "Project is deleted");
        }
        return ProjectResponse.from(p);
    }

    public List<ProjectResponse> getAll() {
        return repo.findByDeletedFalse().stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public List<ProjectResponse> getDeleted() {
        return repo.findByDeletedTrue().stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse update(Long id, ProjectUpdateRequest req) {
        Project p = loadEntity(id);

        if (p.isDeleted()) {
            throw new ApiException(HttpStatus.GONE, "Cannot update deleted project");
        }

        p.setName(req.getName());
        p.setDescription(req.getDescription());

        repo.save(p);

        // AUDIT HOOK — PROJECT_UPDATE
        audit.record(
                "SYSTEM",
                AuditAction.PROJECT_UPDATE,
                "Project",
                p.getId(),
                "Updated project " + p.getName()
        );

        return ProjectResponse.from(p);
    }

    public void delete(Long id) {
        Project p = loadEntity(id);
        p.markDeleted();
        repo.save(p);

        // AUDIT HOOK — PROJECT_DELETE
        audit.record(
                "SYSTEM",
                AuditAction.PROJECT_DELETE,
                "Project",
                id,
                "Soft-deleted project " + id
        );
    }

    public ProjectResponse restore(Long id) {
        Project p = loadEntity(id);
        p.restore();
        repo.save(p);

        // AUDIT HOOK — PROJECT_RESTORE
        audit.record(
                "SYSTEM",
                AuditAction.PROJECT_RESTORE,
                "Project",
                id,
                "Restored project " + id
        );

        return ProjectResponse.from(p);
    }
}
