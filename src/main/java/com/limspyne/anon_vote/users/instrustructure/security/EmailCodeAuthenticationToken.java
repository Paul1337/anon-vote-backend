package com.limspyne.anon_vote.users.instrustructure.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;
import java.util.UUID;

public class EmailCodeAuthenticationToken extends AbstractAuthenticationToken {
    private final String email;
    private final String code;
    private UUID userId;

    public EmailCodeAuthenticationToken(String email, String code) {
        super(null);
        this.email = email;
        this.code = code;
        setAuthenticated(false);
    }

    public EmailCodeAuthenticationToken(String email, String code, UUID userId) {
        super(List.of());
        this.email = email;
        this.code = code;
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return new AppUserDetails(email, userId);
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }
}
