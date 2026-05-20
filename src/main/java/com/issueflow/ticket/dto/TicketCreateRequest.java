package com.issueflow.ticket.dto;

import com.issueflow.ticket.TicketPriority;
import com.issueflow.ticket.TicketStatus;
import com.issueflow.ticket.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class TicketCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private TicketStatus status;

    @NotNull
    private TicketPriority priority;

    @NotNull
    private TicketType type;

    @NotNull
    private Long projectId;

    private Long assigneeId;

    private Instant dueDate;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public TicketType getType() { return type; }
    public Long getProjectId() { return projectId; }
    public Long getAssigneeId() { return assigneeId; }
    public Instant getDueDate() { return dueDate; }
}
