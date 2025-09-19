package com.example.aicodereviewapi.service.user.impl;

import com.example.aicodereviewapi.entity.User;
import com.example.aicodereviewapi.exception.EmailAlreadyExistsException;
import com.example.aicodereviewapi.repository.UserRepository;
import com.example.aicodereviewapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("username already exists");
        }
        User user =
                User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .isGuest(false)
                        .build();
        return userRepository.save(user);
    }

    @Override
    public User createGuestUser() {
        User user = User.builder().isGuest(true).build();
        return userRepository.save(user);
    }

    @Override
    public User findById(String id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }
}
