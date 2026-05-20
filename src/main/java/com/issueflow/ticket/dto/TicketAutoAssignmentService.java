package com.issueflow.ticket;

import com.issueflow.project.Project;
import com.issueflow.user.User;
import com.issueflow.user.UserRole;
import com.issueflow.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TicketAutoAssignmentService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public TicketAutoAssignmentService(UserRepository userRepository,
                                       TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    public User chooseAssignee(Project project) {
        List<User> devs = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.DEVELOPER)
                .toList();

        if (devs.isEmpty()) return null;

        return devs.stream()
                .map(u -> new Object[]{u, ticketRepository.countByProjectAndAssigneeAndStatusNot(
                        project, u, TicketStatus.DONE)})
                .sorted(Comparator
                        .<Object[]>comparingLong(a -> (Long) a[1])
                        .thenComparing(a -> ((User) a[0]).getCreatedAt()))
                .map(a -> (User) a[0])
                .findFirst()
                .orElse(null);
    }
}
