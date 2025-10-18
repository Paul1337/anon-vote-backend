package com.limspyne.anon_vote.users.web.controllers;

import com.limspyne.anon_vote.users.dto.AuthUser;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.instrastructure.security.EmailCodeAuthenticationToken;
import com.limspyne.anon_vote.users.instrastructure.security.JwtTokenProviderService;
import com.limspyne.anon_vote.users.domain.services.SendCodeService;
import com.limspyne.anon_vote.users.web.infrastructure.AuthCookieManager;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProviderService jwtTokenProviderService;

    @Autowired
    private AuthCookieManager authCookieManager;

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        return ResponseEntity.status(HttpStatus.OK).build();
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
