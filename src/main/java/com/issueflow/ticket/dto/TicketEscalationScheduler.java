package com.issueflow.ticket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class TicketEscalationScheduler {

    private final TicketRepository ticketRepository;

    public TicketEscalationScheduler(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Scheduled(cron = "${escalation.cron}")
    public void escalateOverdueTickets() {
        Instant now = Instant.now();
        List<Ticket> candidates =
                ticketRepository.findByDueDateBeforeAndStatusNotAndDeletedFalse(now, TicketStatus.DONE);

        for (Ticket t : candidates) {
            if (t.getPriority() == TicketPriority.CRITICAL) {
                t.setOverdue(true);
                continue;
            }
            t.setPriority(nextPriority(t.getPriority()));
            t.setOverdue(false);
        }
        ticketRepository.saveAll(candidates);
    }

    private TicketPriority nextPriority(TicketPriority p) {
        return switch (p) {
            case LOW -> TicketPriority.MEDIUM;
            case MEDIUM -> TicketPriority.HIGH;
            case HIGH -> TicketPriority.CRITICAL;
            case CRITICAL -> TicketPriority.CRITICAL;
        };
    }
}
