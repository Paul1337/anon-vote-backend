package com.limspyne.anon_vote.users.domain.services;

import com.limspyne.anon_vote.users.domain.entities.User;
import com.limspyne.anon_vote.users.domain.exceptions.UserExistsException;
import com.limspyne.anon_vote.users.instrustructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUserByEmail(String email) {
        if (userRepository.existsByEmail(email)) throw new UserExistsException();
        var newUser = new User(email);
        userRepository.save(newUser);
        return newUser;
    }
}
