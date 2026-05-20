package com.issueflow.comment;

import com.issueflow.comment.dto.CommentCreateRequest;
import com.issueflow.comment.dto.CommentResponse;
import com.issueflow.comment.dto.CommentUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public CommentResponse create(@PathVariable Long ticketId,
                                  @Valid @RequestBody CommentCreateRequest req) {
        return service.create(ticketId, req);
    }

    @GetMapping
    public List<CommentResponse> list(@PathVariable Long ticketId) {
        return service.list(ticketId);
    }

    @PatchMapping("/{id}")
    public CommentResponse update(@PathVariable Long id,
                                  @Valid @RequestBody CommentUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
