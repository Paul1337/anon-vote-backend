package com.limspyne.anon_vote.users.instrastructure.security;

import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EmailCodeAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmailCodeAuthenticationToken token = (EmailCodeAuthenticationToken) authentication;
        String email = token.getEmail();
        String code = token.getCode();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email"));

        boolean confirmationSucess = user.tryConfirmCodeValue(((EmailCodeAuthenticationToken) authentication).getCode());
        if (!confirmationSucess) throw new BadCredentialsException("Confirmation code invalid or expired");
        user.getActiveCodes().clear();
        userRepository.save(user);

        return new EmailCodeAuthenticationToken(email, code, user.getId());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
