package com.issueflow.audit;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;

    public AuditLogService(AuditLogRepository repo) {
        this.repo = repo;
    }

    public void record(String actor, AuditAction action, String entityType, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        repo.save(log);
    }

    public List<AuditLog> getAll() {
        return repo.findAll();
    }

    public List<AuditLog> byEntityType(String type) {
        return repo.findByEntityType(type);
    }

    public List<AuditLog> byEntityId(Long id) {
        return repo.findByEntityId(id);
    }

    public List<AuditLog> byAction(AuditAction action) {
        return repo.findByAction(action);
    }
}
