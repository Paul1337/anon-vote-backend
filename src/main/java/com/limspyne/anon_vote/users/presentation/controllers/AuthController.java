package com.limspyne.anon_vote.users.presentation.controllers;

import com.limspyne.anon_vote.users.dto.AuthUser;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.instrastructure.security.EmailCodeAuthenticationToken;
import com.limspyne.anon_vote.users.instrastructure.security.JwtTokenProviderService;
import com.limspyne.anon_vote.users.domain.services.SendCodeService;
import com.limspyne.anon_vote.users.presentation.infrastructure.AuthCookieManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SendCodeService sendCodeService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProviderService jwtTokenProviderService;

    private final AuthCookieManager authCookieManager;

    @PostMapping("/sendCode")
    public ResponseEntity<Void> sendCode(@RequestBody @Validated SendCode.Request request) {
        sendCodeService.sendCode(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping({ "/", "" })
    public ResponseEntity<Void> authorizeUser(@RequestBody @Validated AuthUser.Request request, HttpServletResponse response) {
        EmailCodeAuthenticationToken authToken = new EmailCodeAuthenticationToken(request.getEmail(), request.getCode());
        Authentication authentication = authenticationManager.authenticate(authToken);

        String token = jwtTokenProviderService.generateToken(authentication);
        String cookieHeader = authCookieManager.createAuthCookieHeader(token);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieHeader).build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMe() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        String deleteCookieHeader = authCookieManager.deleteAuthCookieHeader();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookieHeader).build();
    }
}
