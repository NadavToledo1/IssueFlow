package com.issueflow.ticket;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_dependencies",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ticket_id","blocked_by_id"}))
public class TicketDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blocked_by_id")
    private Ticket blockedBy;

    public Long getId() { return id; }
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public Ticket getBlockedBy() { return blockedBy; }
    public void setBlockedBy(Ticket blockedBy) { this.blockedBy = blockedBy; }
}
