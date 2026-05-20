@Test
void createComment_success() {
    Ticket t = new Ticket();
    t.setId(1L);

    User u = new User();
    u.setId(2L);
    u.setUsername("nadav");

    when(ticketService.loadEntity(1L)).thenReturn(t);
    when(userService.loadEntity(2L)).thenReturn(u);

    Comment saved = new Comment();
    saved.setId(10L);
    saved.setAuthor(u);
    saved.setTicket(t);
    saved.setContent("Hello");

    when(repo.save(any())).thenReturn(saved);

    CommentCreateRequest req = mock(CommentCreateRequest.class);
    when(req.getContent()).thenReturn("Hello");
    when(req.getAuthorId()).thenReturn(2L);

    var res = service.create(1L, req);

    assertEquals(10L, res.getId());
    verify(audit).record(eq("nadav"), eq(AuditAction.COMMENT_CREATE), eq("Comment"), eq(10L), anyString());
}
