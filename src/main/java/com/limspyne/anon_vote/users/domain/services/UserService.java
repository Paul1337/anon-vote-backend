package com.limspyne.anon_vote.users.domain.services;

import com.limspyne.anon_vote.users.domain.entities.User;
import com.limspyne.anon_vote.users.domain.exceptions.UserExistsException;
import com.limspyne.anon_vote.users.domain.exceptions.UserNotFoundException;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).orElseThrow(UserNotFoundException::new);
    }

    public User createUser(String email) {
        if (userRepository.existsByEmail(email)) throw new UserExistsException();
        var newUser = new User(email);
        userRepository.save(newUser);
        return newUser;
    }

    public User createUser(String email, long telegramId) {
        if (userRepository.existsByEmail(email)) throw new UserExistsException();
        var newUser = new User(email, telegramId);
        userRepository.save(newUser);
        return newUser;
    }
}
