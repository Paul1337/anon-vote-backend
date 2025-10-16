package com.limspyne.anon_vote.users.controllers;

import com.limspyne.anon_vote.users.dto.AuthUser;
import com.limspyne.anon_vote.users.dto.SendCode;
import com.limspyne.anon_vote.users.security.EmailCodeAuthenticationToken;
import com.limspyne.anon_vote.users.security.JwtTokenProviderService;
import com.limspyne.anon_vote.users.services.SendCodeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/sendCode")
    public ResponseEntity<Void> sendCode(@RequestBody SendCode.Request request) {
        sendCodeService.sendCode(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping({ "/", "" })
    public ResponseEntity<Void> authorizeUser(@RequestBody AuthUser.Request request, HttpServletResponse response) {
        EmailCodeAuthenticationToken authToken = new EmailCodeAuthenticationToken(request.getEmail(), request.getCode());
        Authentication authentication = authenticationManager.authenticate(authToken);

        String token = jwtTokenProviderService.generateToken(authentication);
        response.setHeader("Set-Cookie", "ACCESS_TOKEN=%s; HttpOnly; SameSite=Lax; Path=/; Max-Age=86400".formatted(token)); //Secure;

        return ResponseEntity.status(HttpStatus.OK).build();
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
}
