package com.limspyne.anon_vote.users.instrustructure.security;

import com.limspyne.anon_vote.users.domain.exceptions.UserNotFoundException;
import com.limspyne.anon_vote.users.instrustructure.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
        return new AppUserDetails(user.getEmail(), user.getId());
    }
}
