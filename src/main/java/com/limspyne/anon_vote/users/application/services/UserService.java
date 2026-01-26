package com.limspyne.anon_vote.users.application.services;

import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.application.exceptions.UserExistsException;
import com.limspyne.anon_vote.users.application.exceptions.UserNotFoundException;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserActiveCodeRepository;
import com.limspyne.anon_vote.users.instrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserActiveCodeRepository userActiveCodeRepository;

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void createIfAbsent(String email) {
        if (userRepository.existsByEmail(email)) return;
        userRepository.save(new User(email));
    }

    @Transactional
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

    @Transactional
    public void deleteByTelegramId(long telegramId) {
        var userOptional = userRepository.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) return;
        var user = userOptional.get();
        userRepository.delete(user);
    }
}
