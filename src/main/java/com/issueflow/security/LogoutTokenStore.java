package com.issueflow.security;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class LogoutTokenStore {

    private final Set<String> invalidTokens = new HashSet<>();

    public void invalidate(String token) {
        invalidTokens.add(token);
    }

    public boolean isInvalid(String token) {
        return invalidTokens.contains(token);
    }
}
