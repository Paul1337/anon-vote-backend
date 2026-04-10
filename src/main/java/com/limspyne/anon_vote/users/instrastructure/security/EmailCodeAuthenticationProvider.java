package com.limspyne.anon_vote.users.instrastructure.security;

import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EmailCodeAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmailCodeAuthenticationToken token = (EmailCodeAuthenticationToken) authentication;

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email"));

        boolean confirmationSucess = user.tryConfirmCodeValue(token.getCode());
        if (!confirmationSucess) throw new BadCredentialsException("Confirmation code invalid or expired");
        user.getActiveCodes().clear();
        userRepository.save(user);

        return new EmailCodeAuthenticationToken(token.getEmail(), token.getCode(), user.getId());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
