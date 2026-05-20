package com.issueflow.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketDependencyRepository extends JpaRepository<TicketDependency, Long> {

    List<TicketDependency> findByTicket(Ticket ticket);

    boolean existsByTicketAndBlockedBy(Ticket ticket, Ticket blockedBy);

    void deleteByTicketAndBlockedBy(Ticket ticket, Ticket blockedBy);
}
