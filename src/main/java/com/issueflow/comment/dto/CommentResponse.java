package com.issueflow.comment.dto;

import com.issueflow.comment.Comment;
import com.issueflow.mention.Mention;

import java.time.Instant;
import java.util.List;

public class CommentResponse {

    private Long id;
    private String content;
    private Long authorId;
    private Long ticketId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<MentionInfo> mentionedUsers;

    public static CommentResponse from(Comment c, List<Mention> mentions) {
        CommentResponse r = new CommentResponse();
        r.id = c.getId();
        r.content = c.getContent();
        r.authorId = c.getAuthor().getId();
        r.ticketId = c.getTicket().getId();
        r.createdAt = c.getCreatedAt();
        r.updatedAt = c.getUpdatedAt();
        r.mentionedUsers = mentions.stream()
                .map(m -> new MentionInfo(
                        m.getUser().getId(),
                        m.getUser().getUsername(),
                        m.getUser().getFullName()))
                .toList();
        return r;
    }

    public record MentionInfo(Long id, String username, String fullName) {}
}
