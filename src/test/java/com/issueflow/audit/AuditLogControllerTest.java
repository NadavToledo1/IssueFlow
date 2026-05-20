@WebMvcTest(AuditLogController.class)
class AuditLogControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    AuditLogService service;

    @Test
    void getAll_returns200() throws Exception {
        mvc.perform(get("/audit"))
                .andExpect(status().isOk());
    }
}
