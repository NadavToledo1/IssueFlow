package com.issueflow.comment;

import com.issueflow.common.BaseEntity;
import com.issueflow.ticket.Ticket;
import com.issueflow.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 4000)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Version
    private Long version;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Long getVersion() { return version; }
}
