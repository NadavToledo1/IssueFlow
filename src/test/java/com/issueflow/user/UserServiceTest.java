package com.issueflow.user;

import com.issueflow.audit.AuditLogService;
import com.issueflow.common.ApiException;
import com.issueflow.user.dto.UserCreateRequest;
import com.issueflow.user.dto.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repo;
    private PasswordEncoder encoder;
    private AuditLogService audit;
    private UserService service;

    @BeforeEach
    void setup() {
        repo = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        audit = mock(AuditLogService.class);
        service = new UserService(repo, encoder, audit);
    }

    @Test
    void createUser_success() {
        UserCreateRequest req = mock(UserCreateRequest.class);
        when(req.getUsername()).thenReturn("nadav");
        when(req.getEmail()).thenReturn("n@n.com");
        when(req.getFullName()).thenReturn("Nadav");
        when(req.getRole()).thenReturn(UserRole.DEVELOPER);
        when(req.getPassword()).thenReturn("123");

        when(repo.existsByUsername("nadav")).thenReturn(false);
        when(repo.existsByEmail("n@n.com")).thenReturn(false);
        when(encoder.encode("123")).thenReturn("HASH");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("nadav");
        when(repo.save(any())).thenReturn(saved);

        var res = service.create(req);

        assertEquals("nadav", res.getUsername());
        verify(audit, times(1)).record(
                eq("nadav"),
                any(),
                eq("User"),
                eq(1L),
                anyString()
        );
    }

    @Test
    void updateUser_notFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> service.update(99L, new UserUpdateRequest()));
    }
}
