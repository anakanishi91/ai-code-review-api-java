package com.example.aicodereviewapi.repository;

import com.example.aicodereviewapi.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String username);
}
