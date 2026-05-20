package com.issueflow.ticket;

import com.issueflow.ticket.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    public TicketResponse create(@Valid @RequestBody TicketCreateRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public TicketResponse update(@PathVariable Long id,
                                 @Valid @RequestBody TicketUpdateRequest req) {
        TicketResponse current = service.get(id);
        if (req.getStatus() == TicketStatus.DONE) {
            service.ensureNoOpenBlockers(service.loadEntity(id));
        }
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    public List<TicketResponse> byProject(@RequestParam Long projectId) {
        return service.getByProject(projectId);
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketResponse> deletedByProject(@RequestParam Long projectId) {
        return service.getDeletedByProject(projectId);
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public TicketResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }

    // Dependencies

    @PostMapping("/{ticketId}/dependencies")
    public void addDependency(@PathVariable Long ticketId,
                              @RequestBody DependencyRequest body) {
        service.addDependency(ticketId, body.blockedBy());
    }

    @GetMapping("/{ticketId}/dependencies")
    public List<TicketResponse> listDependencies(@PathVariable Long ticketId) {
        return service.listDependencies(ticketId);
    }

    @DeleteMapping("/{ticketId}/dependencies/{blockerId}")
    public void removeDependency(@PathVariable Long ticketId,
                                 @PathVariable Long blockerId) {
        service.removeDependency(ticketId, blockerId);
    }

    // CSV

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam Long projectId) {
        byte[] csv = service.exportCsv(projectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tickets.csv\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TicketImportResult importCsv(@RequestParam Long projectId,
                                        @RequestPart("file") MultipartFile file) {
        return service.importCsv(projectId, file);
    }

    public record DependencyRequest(Long blockedBy) {}
}
