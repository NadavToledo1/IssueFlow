package com.issueflow.comment;

import com.issueflow.audit.AuditAction;
import com.issueflow.audit.AuditLogService;
import com.issueflow.common.ApiException;
import com.issueflow.comment.dto.CommentCreateRequest;
import com.issueflow.comment.dto.CommentResponse;
import com.issueflow.comment.dto.CommentUpdateRequest;
import com.issueflow.mention.Mention;
import com.issueflow.mention.MentionService;
import com.issueflow.ticket.Ticket;
import com.issueflow.ticket.TicketService;
import com.issueflow.user.User;
import com.issueflow.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repo;
    private final TicketService ticketService;
    private final UserService userService;
    private final MentionService mentionService;
    private final AuditLogService audit;

    public CommentService(CommentRepository repo,
                          TicketService ticketService,
                          UserService userService,
                          MentionService mentionService,
                          AuditLogService audit) {
        this.repo = repo;
        this.ticketService = ticketService;
        this.userService = userService;
        this.mentionService = mentionService;
        this.audit = audit;
    }

    public CommentResponse create(Long ticketId, CommentCreateRequest req) {
        Ticket t = ticketService.loadEntity(ticketId);
        User author = userService.loadEntity(req.getAuthorId());

        Comment c = new Comment();
        c.setContent(req.getContent());
        c.setTicket(t);
        c.setAuthor(author);

        repo.save(c);

        mentionService.updateMentions(c, req.getContent());

        // AUDIT HOOK — COMMENT_CREATE
        audit.record(
                author.getUsername(),
                AuditAction.COMMENT_CREATE,
                "Comment",
                c.getId(),
                "Created comment on ticket " + ticketId
        );

        return CommentResponse.from(c, mentionService.getMentions(c));
    }

    public List<CommentResponse> list(Long ticketId) {
        Ticket t = ticketService.loadEntity(ticketId);
        return repo.findByTicketOrderByCreatedAtAsc(t).stream()
                .map(c -> CommentResponse.from(c, mentionService.getMentions(c)))
                .toList();
    }

    public CommentResponse update(Long id, CommentUpdateRequest req) {
        Comment c = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment not found"));

        c.setContent(req.getContent());
        repo.save(c);

        mentionService.updateMentions(c, req.getContent());

        // AUDIT HOOK — COMMENT_UPDATE
        audit.record(
                "SYSTEM",
                AuditAction.COMMENT_UPDATE,
                "Comment",
                c.getId(),
                "Updated comment " + c.getId()
        );

        return CommentResponse.from(c, mentionService.getMentions(c));
    }

    public void delete(Long id) {
        Comment c = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment not found"));

        repo.delete(c);

        // AUDIT HOOK — COMMENT_DELETE
        audit.record(
                "SYSTEM",
                AuditAction.COMMENT_DELETE,
                "Comment",
                id,
                "Deleted comment " + id
        );
    }
}
