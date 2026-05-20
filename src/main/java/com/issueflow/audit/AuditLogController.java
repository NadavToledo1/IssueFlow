package com.issueflow.audit;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditLog> all() {
        return service.getAll();
    }

    @GetMapping("/entityType/{type}")
    public List<AuditLog> byType(@PathVariable String type) {
        return service.byEntityType(type);
    }

    @GetMapping("/entityId/{id}")
    public List<AuditLog> byEntity(@PathVariable Long id) {
        return service.byEntityId(id);
    }

    @GetMapping("/action/{action}")
    public List<AuditLog> byAction(@PathVariable AuditAction action) {
        return service.byAction(action);
    }
}
