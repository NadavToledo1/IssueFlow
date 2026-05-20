package com.issueflow.ticket.dto;

import com.issueflow.ticket.TicketPriority;
import com.issueflow.ticket.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class TicketUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private TicketStatus status;

    @NotNull
    private TicketPriority priority;

    private Long assigneeId;

    private Instant dueDate;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public Long getAssigneeId() { return assigneeId; }
    public Instant getDueDate() { return dueDate; }
}
