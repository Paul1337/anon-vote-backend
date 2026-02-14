package com.limspyne.anon_vote.users.instrastructure.security;

import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.application.exceptions.UserNotFoundException;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            var email = userDetails.getUsername();
            return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }
    }
}