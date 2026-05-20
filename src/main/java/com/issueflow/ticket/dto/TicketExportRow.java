package com.issueflow.ticket.dto;

import com.issueflow.ticket.Ticket;

public class TicketExportRow {

    public static String[] header() {
        return new String[]{"id","title","description","status","priority","type","assigneeId"};
    }

    public static String[] from(Ticket t) {
        return new String[]{
                String.valueOf(t.getId()),
                t.getTitle(),
                t.getDescription(),
                t.getStatus().name(),
                t.getPriority().name(),
                t.getType().name(),
                t.getAssignee() != null ? String.valueOf(t.getAssignee().getId()) : ""
        };
    }
}
