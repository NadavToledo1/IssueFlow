package com.issueflow.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentCreateRequest {

    @NotBlank
    private String content;

    @NotNull
    private Long authorId;

    public String getContent() { return content; }
    public Long getAuthorId() { return authorId; }
}
