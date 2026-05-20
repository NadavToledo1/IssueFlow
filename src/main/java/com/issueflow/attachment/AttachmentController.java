package com.issueflow.attachment;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tickets/{ticketId}/attachments")
public class AttachmentController {

    private final AttachmentService service;

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Attachment upload(@PathVariable Long ticketId,
                             @RequestParam @NotNull Long userId,
                             @RequestPart("file") MultipartFile file) {
        return service.upload(ticketId, userId, file);
    }

    @GetMapping
    public List<Attachment> list(@PathVariable Long ticketId) {
        return service.list(ticketId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Attachment a = service.list(a -> true)
                .stream()
                .filter(att -> att.getId().equals(id))
                .findFirst()
                .orElse(null);

        byte[] data = service.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + (a != null ? a.getFilename() : "file") + "\"")
                .contentType(MediaType.parseMediaType(a != null ? a.getContentType() : "application/octet-stream"))
                .body(data);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
