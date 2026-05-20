package com.issueflow.project;

import com.issueflow.project.dto.ProjectCreateRequest;
import com.issueflow.project.dto.ProjectResponse;
import com.issueflow.project.dto.ProjectUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody ProjectCreateRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<ProjectResponse> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProjectUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ADMIN ONLY
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProjectResponse> getDeleted() {
        return service.getDeleted();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }
}
