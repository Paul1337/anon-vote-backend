package com.limspyne.anon_vote.users.presenter.infrastructure;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieManager {
    private final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    private final boolean COOKIE_SECURE = false;

    public String createAuthCookieHeader(String token) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build()
                .toString();
    }

    public String deleteAuthCookieHeader() {
        return createAuthCookieHeader("");
    }
}
