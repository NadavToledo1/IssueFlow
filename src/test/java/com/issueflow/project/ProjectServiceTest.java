@Test
void createProject_success() {
    ProjectCreateRequest req = mock(ProjectCreateRequest.class);
    when(req.getName()).thenReturn("IssueFlow");
    when(req.getDescription()).thenReturn("Tracker");
    when(req.getOwnerId()).thenReturn(1L);

    User owner = new User();
    owner.setId(1L);
    owner.setUsername("nadav");

    when(userService.loadEntity(1L)).thenReturn(owner);

    Project saved = new Project();
    saved.setId(10L);
    saved.setName("IssueFlow");

    when(repo.save(any())).thenReturn(saved);

    var res = service.create(req);

    assertEquals("IssueFlow", res.getName());
    verify(audit).record(eq("nadav"), any(), eq("Project"), eq(10L), anyString());
}
