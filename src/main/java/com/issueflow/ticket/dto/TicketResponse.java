package com.issueflow.ticket.dto;

import com.issueflow.ticket.Ticket;
import com.issueflow.ticket.TicketPriority;
import com.issueflow.ticket.TicketStatus;
import com.issueflow.ticket.TicketType;

import java.time.Instant;

public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketType type;
    private Long projectId;
    private Long assigneeId;
    private Instant dueDate;
    private boolean overdue;
    private boolean deleted;

    public static TicketResponse from(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.id = t.getId();
        r.title = t.getTitle();
        r.description = t.getDescription();
        r.status = t.getStatus();
        r.priority = t.getPriority();
        r.type = t.getType();
        r.projectId = t.getProject().getId();
        r.assigneeId = t.getAssignee() != null ? t.getAssignee().getId() : null;
        r.dueDate = t.getDueDate();
        r.overdue = t.isOverdue();
        r.deleted = t.isDeleted();
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public TicketType getType() { return type; }
    public Long getProjectId() { return projectId; }
    public Long getAssigneeId() { return assigneeId; }
    public Instant getDueDate() { return dueDate; }
    public boolean isOverdue() { return overdue; }
    public boolean isDeleted() { return deleted; }
}
