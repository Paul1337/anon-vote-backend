package com.limspyne.anon_vote.users.presenter.controllers;

import com.limspyne.anon_vote.users.application.services.UserService;
import com.limspyne.anon_vote.users.dto.AuthUser;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.instrastructure.security.EmailCodeAuthenticationToken;
import com.limspyne.anon_vote.users.instrastructure.security.JwtTokenProviderService;
import com.limspyne.anon_vote.users.application.services.SendCodeService;
import com.limspyne.anon_vote.users.presenter.infrastructure.AuthCookieManager;
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

    private final UserService userService;

    @PostMapping("/sendCode")
    public ResponseEntity<Void> sendCode(@RequestBody @Validated SendCode.Request request) {
        userService.createIfAbsent(request.getEmail());
        sendCodeService.sendCode(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping({ "/", "" })
    public ResponseEntity<Void> authenticateUser(@RequestBody @Validated AuthUser.Request request) {
        EmailCodeAuthenticationToken authToken = new EmailCodeAuthenticationToken(request.getEmail(), request.getCode());
        Authentication authentication = authenticationManager.authenticate(authToken);

        String token = jwtTokenProviderService.generateToken(authentication);
        String cookieHeader = authCookieManager.createAuthCookieHeader(token);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookieHeader).build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> checkAuth() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        String deleteCookieHeader = authCookieManager.deleteAuthCookieHeader();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookieHeader).build();
    }
}
