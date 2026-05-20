package com.issueflow.ticket.dto;

import java.util.ArrayList;
import java.util.List;

public class TicketImportResult {

    private int created;
    private int failed;
    private List<String> errors = new ArrayList<>();

    public void incCreated() { created++; }
    public void incFailed(String error) { failed++; errors.add(error); }

    public int getCreated() { return created; }
    public int getFailed() { return failed; }
    public List<String> getErrors() { return errors; }
}
