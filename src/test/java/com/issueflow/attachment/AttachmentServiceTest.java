@Test
void uploadAttachment_success() throws Exception {
    Ticket t = new Ticket();
    t.setId(1L);

    User u = new User();
    u.setId(2L);
    u.setUsername("nadav");

    when(ticketService.loadEntity(1L)).thenReturn(t);
    when(userService.loadEntity(2L)).thenReturn(u);

    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("file.txt");
    when(file.getContentType()).thenReturn("text/plain");
    when(file.getSize()).thenReturn(100L);

    Attachment saved = new Attachment();
    saved.setId(50L);
    saved.setFilename("file.txt");

    when(repo.save(any())).thenReturn(saved);

    var res = service.upload(1L, 2L, file);

    assertEquals(50L, res.getId());
    verify(audit).record(eq("nadav"), eq(AuditAction.ATTACHMENT_UPLOAD), eq("Attachment"), eq(50L), anyString());
}
