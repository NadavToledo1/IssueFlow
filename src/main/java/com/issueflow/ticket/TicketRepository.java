package com.issueflow.ticket;

import com.issueflow.project.Project;
import com.issueflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByProjectAndDeletedFalse(Project project);

    List<Ticket> findByProjectAndDeletedTrue(Project project);

    long countByProjectAndAssigneeAndStatusNot(Project project, User assignee, TicketStatus status);

    List<Ticket> findByDueDateBeforeAndStatusNotAndDeletedFalse(Instant now, TicketStatus status);
}
