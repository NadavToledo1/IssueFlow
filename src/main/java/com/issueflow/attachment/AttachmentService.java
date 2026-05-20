package com.issueflow.attachment;

import com.issueflow.audit.AuditAction;
import com.issueflow.audit.AuditLogService;
import com.issueflow.common.ApiException;
import com.issueflow.ticket.Ticket;
import com.issueflow.ticket.TicketService;
import com.issueflow.user.User;
import com.issueflow.user.UserService;
import com.issueflow.util.FileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepository repo;
    private final TicketService ticketService;
    private final UserService userService;
    private final FileValidator validator;
    private final AuditLogService audit;

    @Value("${file.storage-dir}")
    private String storageDir;

    public AttachmentService(AttachmentRepository repo,
                             TicketService ticketService,
                             UserService userService,
                             FileValidator validator,
                             AuditLogService audit) {
        this.repo = repo;
        this.ticketService = ticketService;
        this.userService = userService;
        this.validator = validator;
        this.audit = audit;
    }

    public Attachment upload(Long ticketId, Long userId, MultipartFile file) {
        validator.validate(file);

        Ticket ticket = ticketService.loadEntity(ticketId);
        User user = userService.loadEntity(userId);

        try {
            Files.createDirectories(Path.of(storageDir));
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create storage directory");
        }

        String storedName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Path.of(storageDir, storedName);

        try {
            file.transferTo(path);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }

        Attachment a = new Attachment();
        a.setFilename(file.getOriginalFilename());
        a.setContentType(file.getContentType());
        a.setSize(file.getSize());
        a.setStoragePath(path.toString());
        a.setTicket(ticket);
        a.setUploadedBy(user);

        repo.save(a);

        // AUDIT HOOK — ATTACHMENT_UPLOAD
        audit.record(
                user.getUsername(),
                AuditAction.ATTACHMENT_UPLOAD,
                "Attachment",
                a.getId(),
                "Uploaded file " + a.getFilename() + " to ticket " + ticketId
        );

        return a;
    }

    public List<Attachment> list(Long ticketId) {
        Ticket t = ticketService.loadEntity(ticketId);
        return repo.findByTicket(t);
    }

    public byte[] download(Long id) {
        Attachment a = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attachment not found"));

        try {
            return Files.readAllBytes(Path.of(a.getStoragePath()));
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read file");
        }
    }

    public void delete(Long id) {
        Attachment a = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attachment not found"));

        try {
            Files.deleteIfExists(Path.of(a.getStoragePath()));
        } catch (IOException ignored) {}

        repo.delete(a);

        // AUDIT HOOK — ATTACHMENT_DELETE
        audit.record(
                "SYSTEM",
                AuditAction.ATTACHMENT_DELETE,
                "Attachment",
                id,
                "Deleted attachment " + id + " (" + a.getFilename() + ")"
        );
    }
}
