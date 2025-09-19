package com.example.aicodereviewapi.service.user;

import com.example.aicodereviewapi.entity.User;

public interface UserService {
    User register(String email, String password);

    User createGuestUser();

    User findById(String id);

    User findByEmail(String email);
}
