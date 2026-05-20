@Test
void createTicket_autoAssign() {
    TicketCreateRequest req = mock(TicketCreateRequest.class);
    when(req.getProjectId()).thenReturn(1L);
    when(req.getTitle()).thenReturn("Bug");
    when(req.getDescription()).thenReturn("Fix it");
    when(req.getStatus()).thenReturn(TicketStatus.TODO);
    when(req.getPriority()).thenReturn(TicketPriority.LOW);
    when(req.getType()).thenReturn(TicketType.BUG);

    Project p = new Project();
    p.setId(1L);

    when(projectService.loadEntity(1L)).thenReturn(p);

    User dev = new User();
    dev.setId(5L);
    dev.setUsername("dev1");

    when(autoAssignmentService.chooseAssignee(p)).thenReturn(dev);

    Ticket saved = new Ticket();
    saved.setId(100L);
    saved.setTitle("Bug");
    saved.setAssignee(dev);

    when(repo.save(any())).thenReturn(saved);

    var res = service.create(req);

    assertEquals(100L, res.getId());
    verify(audit).record(eq("dev1"), eq(AuditAction.TICKET_CREATE), eq("Ticket"), eq(100L), anyString());
}
