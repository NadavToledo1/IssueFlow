package com.issueflow.ticket;

import com.issueflow.audit.AuditAction;
import com.issueflow.audit.AuditLogService;
import com.issueflow.common.ApiException;
import com.issueflow.project.Project;
import com.issueflow.project.ProjectService;
import com.issueflow.ticket.dto.*;
import com.issueflow.user.User;
import com.issueflow.user.UserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository repo;
    private final TicketDependencyRepository depRepo;
    private final ProjectService projectService;
    private final UserService userService;
    private final TicketAutoAssignmentService autoAssignmentService;
    private final AuditLogService audit;

    public TicketService(TicketRepository repo,
                         TicketDependencyRepository depRepo,
                         ProjectService projectService,
                         UserService userService,
                         TicketAutoAssignmentService autoAssignmentService,
                         AuditLogService audit) {
        this.repo = repo;
        this.depRepo = depRepo;
        this.projectService = projectService;
        this.userService = userService;
        this.autoAssignmentService = autoAssignmentService;
        this.audit = audit;
    }

    public TicketResponse create(TicketCreateRequest req) {
        Project project = projectService.loadEntity(req.getProjectId());

        Ticket t = new Ticket();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setStatus(req.getStatus());
        t.setPriority(req.getPriority());
        t.setType(req.getType());
        t.setProject(project);
        t.setDueDate(req.getDueDate());

        if (req.getAssigneeId() != null) {
            t.setAssignee(userService.loadEntity(req.getAssigneeId()));
        } else {
            User auto = autoAssignmentService.chooseAssignee(project);
            t.setAssignee(auto);
        }

        repo.save(t);

        // AUDIT HOOK — TICKET_CREATE
        audit.record(
                t.getAssignee() != null ? t.getAssignee().getUsername() : "SYSTEM",
                AuditAction.TICKET_CREATE,
                "Ticket",
                t.getId(),
                "Created ticket " + t.getTitle()
        );

        return TicketResponse.from(t);
    }

    public Ticket loadEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public TicketResponse get(Long id) {
        Ticket t = loadEntity(id);
        if (t.isDeleted()) {
            throw new ApiException(HttpStatus.GONE, "Ticket is deleted");
        }
        return TicketResponse.from(t);
    }

    public List<TicketResponse> getByProject(Long projectId) {
        Project p = projectService.loadEntity(projectId);
        return repo.findByProjectAndDeletedFalse(p).stream()
                .map(TicketResponse::from)
                .toList();
    }

    public List<TicketResponse> getDeletedByProject(Long projectId) {
        Project p = projectService.loadEntity(projectId);
        return repo.findByProjectAndDeletedTrue(p).stream()
                .map(TicketResponse::from)
                .toList();
    }

    public TicketResponse update(Long id, TicketUpdateRequest req) {
        Ticket t = loadEntity(id);

        if (t.isDeleted()) {
            throw new ApiException(HttpStatus.GONE, "Cannot update deleted ticket");
        }
        if (t.getStatus() == TicketStatus.DONE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ticket is DONE and cannot be updated");
        }

        ensureForwardStatus(t.getStatus(), req.getStatus());

        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setStatus(req.getStatus());
        t.setPriority(req.getPriority());
        t.setDueDate(req.getDueDate());
        t.setOverdue(false); // manual change resets auto escalation

        if (req.getAssigneeId() != null) {
            t.setAssignee(userService.loadEntity(req.getAssigneeId()));
        }

        repo.save(t);

        // AUDIT HOOK — TICKET_UPDATE
        audit.record(
                "SYSTEM",
                AuditAction.TICKET_UPDATE,
                "Ticket",
                t.getId(),
                "Updated ticket " + t.getTitle()
        );

        return TicketResponse.from(t);
    }

    private void ensureForwardStatus(TicketStatus current, TicketStatus next) {
        if (current == next) return;
        if (current == TicketStatus.TODO && next == TicketStatus.IN_PROGRESS) return;
        if (current == TicketStatus.IN_PROGRESS && next == TicketStatus.IN_REVIEW) return;
        if (current == TicketStatus.IN_REVIEW && next == TicketStatus.DONE) return;
        throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status transition");
    }

    public void delete(Long id) {
        Ticket t = loadEntity(id);
        t.markDeleted();
        repo.save(t);

        // AUDIT HOOK — TICKET_DELETE
        audit.record(
                "SYSTEM",
                AuditAction.TICKET_DELETE,
                "Ticket",
                id,
                "Soft-deleted ticket " + id
        );
    }

    public TicketResponse restore(Long id) {
        Ticket t = loadEntity(id);
        t.restore();
        repo.save(t);

        // AUDIT HOOK — TICKET_RESTORE
        audit.record(
                "SYSTEM",
                AuditAction.TICKET_RESTORE,
                "Ticket",
                id,
                "Restored ticket " + id
        );

        return TicketResponse.from(t);
    }

    // Dependencies

    public void addDependency(Long ticketId, Long blockerId) {
        Ticket t = loadEntity(ticketId);
        Ticket blocker = loadEntity(blockerId);

        if (!t.getProject().getId().equals(blocker.getProject().getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Tickets must be in same project");
        }
        if (depRepo.existsByTicketAndBlockedBy(t, blocker)) return;

        TicketDependency d = new TicketDependency();
        d.setTicket(t);
        d.setBlockedBy(blocker);
        depRepo.save(d);

        // AUDIT HOOK — dependency added
        audit.record(
                "SYSTEM",
                AuditAction.TICKET_UPDATE,
                "Ticket",
                ticketId,
                "Added dependency: blocked by " + blockerId
        );
    }

    public List<TicketResponse> listDependencies(Long ticketId) {
        Ticket t = loadEntity(ticketId);
        return depRepo.findByTicket(t).stream()
                .map(TicketDependency::getBlockedBy)
                .map(TicketResponse::from)
                .toList();
    }

    public void removeDependency(Long ticketId, Long blockerId) {
        Ticket t = loadEntity(ticketId);
        Ticket blocker = loadEntity(blockerId);
        depRepo.deleteByTicketAndBlockedBy(t, blocker);

        // AUDIT HOOK — dependency removed
        audit.record(
                "SYSTEM",
                AuditAction.TICKET_UPDATE,
                "Ticket",
                ticketId,
                "Removed dependency: blocker " + blockerId
        );
    }

    public void ensureNoOpenBlockers(Ticket t) {
        boolean hasOpen = depRepo.findByTicket(t).stream()
                .map(TicketDependency::getBlockedBy)
                .anyMatch(b -> b.getStatus() != TicketStatus.DONE);
        if (hasOpen) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ticket has unresolved blockers");
        }
    }

    // CSV export/import

    public byte[] exportCsv(Long projectId) {
        Project p = projectService.loadEntity(projectId);
        List<Ticket> tickets = repo.findByProjectAndDeletedFalse(p);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(TicketExportRow.header()))) {

            for (Ticket t : tickets) {
                printer.printRecord((Object[]) TicketExportRow.from(t));
            }
            printer.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to export CSV");
        }
    }

    public TicketImportResult importCsv(Long projectId, MultipartFile file) {
        Project p = projectService.loadEntity(projectId);
        TicketImportResult result = new TicketImportResult();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord r : records) {
                try {
                    Ticket t = new Ticket();
                    t.setTitle(r.get("title"));
                    t.setDescription(r.get("description"));
                    t.setStatus(TicketStatus.valueOf(r.get("status")));
                    t.setPriority(TicketPriority.valueOf(r.get("priority")));
                    t.setType(TicketType.valueOf(r.get("type")));
                    t.setProject(p);

                    String assigneeIdStr = r.get("assigneeId");
                    if (assigneeIdStr != null && !assigneeIdStr.isBlank()) {
                        Long assigneeId = Long.parseLong(assigneeIdStr);
                        t.setAssignee(userService.loadEntity(assigneeId));
                    }

                    repo.save(t);
                    result.incCreated();

                    // AUDIT HOOK — CSV import
                    audit.record(
                            "SYSTEM",
                            AuditAction.TICKET_CREATE,
                            "Ticket",
                            t.getId(),
                            "Imported ticket from CSV"
                    );

                } catch (Exception ex) {
                    result.incFailed("Row " + r.getRecordNumber() + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid CSV file");
        }

        return result;
    }
}
